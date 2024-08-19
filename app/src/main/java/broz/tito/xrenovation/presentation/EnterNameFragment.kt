package broz.tito.xrenovation.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentEnterNameBinding
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.EnterNameViewModel
import broz.tito.xrenovation.presentation.models.EnterNameViewModelFactory
import javax.inject.Inject


class EnterNameFragment : Fragment(), ProgressBarAble, SnackBarAble {

    private val TAG = "EnterNameFragment"

    private lateinit var binding: FragmentEnterNameBinding

    @Inject
    lateinit var enterNameViewModelFactory: EnterNameViewModelFactory

    lateinit var enterNameViewModel: EnterNameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // FUCK YOU, STUPID NAVIGATION COMPONENT X2
        }
        enterNameViewModel =
            ViewModelProvider(this, enterNameViewModelFactory)[EnterNameViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterNameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonEnterName.setOnClickListener {
            enterNameViewModel.setAccountInfo(
                requireContext(),
                binding.editTextTextPersonName.text.toString(),
                null,
                null
            )
        }
        enterNameViewModel.setAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSetAccountInfoResult -> {
                    showProgressBar()
                }

                is SuccessSetAccountInfoResult -> {
                    Log.d(TAG, "Success -- ${it.response.displayName} -- ${it.response.email}")
                    hideProgressBar()
                    findNavController().navigate(R.id.action_enterNameFragment_to_accountInfoFragment)
                }

                is FailureSetAccountInfoResult -> {
                    if (it.errorMessage == "INVALID_ID_TOKEN") {
                        enterNameViewModel.refreshToken(requireContext())
                    } else if (it.errorMessage == "USER_NOT_FOUND") {
                        hideProgressBar()
                        findNavController().navigateUp()
                    } else {
                        hideProgressBar()
                        showSnackBarShort(
                            this,
                            binding.enterNameFragmentLayout,
                            getString(R.string.error_try_again)
                        )
                    }
                    hideProgressBar()
                }
            }
        }
        enterNameViewModel.refreshTokenResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingRefreshTokenResult -> {
                    // Nothing to do here
                }

                is SuccessRefreshTokenResult -> {
                    enterNameViewModel.setAccountInfo(
                        requireContext(),
                        binding.editTextTextPersonName.text.toString(),
                        null,
                        null
                    )
                }

                is FailureRefreshTokenResult -> {
                    showSnackBarShort(
                        this,
                        binding.enterNameFragmentLayout,
                        getString(R.string.error_try_again)
                    )
                }
            }

        }
    }

    override fun showProgressBar() {
        binding.buttonEnterName.isIndeterminateProgressMode = true
        binding.buttonEnterName.progress = 66
    }

    override fun hideProgressBar() {
        binding.buttonEnterName.progress = 0
    }
}