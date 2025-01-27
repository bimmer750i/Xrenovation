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
import broz.tito.xrenovation.presentation.interfaces.Disablable
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.EnterNameViewModel
import broz.tito.xrenovation.presentation.models.EnterNameViewModelFactory
import javax.inject.Inject


class EnterNameFragment : Fragment(), ProgressBarAble, SnackBarAble,Disablable {

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
            if (!binding.buttonEnterName.isIndeterminateProgressMode) {
                enterNameViewModel.setAccountInfo(
                    requireContext(),
                    binding.editTextTextPersonName.text.toString(),
                    null,
                    null
                )
            }
        }
        enterNameViewModel.setAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSetAccountInfoResult -> {
                    showProgressBar()
                    disableViews()
                }

                is SuccessSetAccountInfoResult -> {
                    Log.d(TAG, "Success -- ${it.response.displayName} -- ${it.response.email}")
                    hideProgressBar()
                    enableViews()
                    findNavController().navigate(R.id.action_enterNameFragment_to_accountInfoFragment)
                }

                is FailureSetAccountInfoResult -> {
                    if (it.errorMessage == INVALID_ID_TOKEN) {
                        enterNameViewModel.refreshToken(requireContext())
                    }
                    else if (it.errorMessage == USER_NOT_FOUND) {
                        hideProgressBar()
                        enableViews()
                        findNavController().navigateUp()
                        enterNameViewModel.resetState()
                    } else {
                        hideProgressBar()
                        enableViews()
                        showSnackBarShort(
                            this,
                            binding.enterNameFragmentLayout,
                            getString(R.string.error_try_again)
                        )
                        enterNameViewModel.resetState()
                    }
                }
            }
        }
        enterNameViewModel.refreshTokenResult.observe(viewLifecycleOwner) {
            when (it) {
                is SuccessRefreshTokenResult -> {
                    enterNameViewModel.setAccountInfo(
                        requireContext(),
                        binding.editTextTextPersonName.text.toString(),
                        null,
                        null
                    )
                }

                is FailureRefreshTokenResult -> {
                    hideProgressBar()
                    enableViews()
                    showSnackBarShort(
                        this,
                        binding.enterNameFragmentLayout,
                        getString(R.string.error_try_again)
                    )
                    enterNameViewModel.resetState()
                }
            }

        }
    }

    override fun showProgressBar() {
        binding.buttonEnterName.isIndeterminateProgressMode = true
        binding.buttonEnterName.progress = 66
    }

    override fun hideProgressBar() {
        binding.buttonEnterName.isIndeterminateProgressMode = false
        binding.buttonEnterName.progress = 0
    }

    override fun enableViews() {
        binding.editTextTextPersonName.isEnabled = true
    }

    override fun disableViews() {
        binding.editTextTextPersonName.isEnabled = false
    }
}