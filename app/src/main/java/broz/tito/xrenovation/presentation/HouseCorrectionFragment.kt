package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.FailureAddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.HouseCorrection
import broz.tito.xrenovation.data.add_house.entities.PendingAddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.SuccessAddHouseCorrectionResult
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentHouseCorrectionBinding
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.HouseCorrectionFragmentViewModel
import broz.tito.xrenovation.presentation.models.HouseCorrectionFragmentViewModelFactory
import javax.inject.Inject

class HouseCorrectionFragment : Fragment(),SnackBarAble {

    private val args : HouseCorrectionFragmentArgs? by navArgs()

    private lateinit var binding : FragmentHouseCorrectionBinding

    private var houseId : String? = null

    private lateinit var viewModel: HouseCorrectionFragmentViewModel

    @Inject
    lateinit var viewModelFactory : HouseCorrectionFragmentViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(viewModelStore,viewModelFactory)[HouseCorrectionFragmentViewModel::class.java]
        args?.let {
            houseId = it.houseId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseCorrectionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSendCorrection.setOnClickListener {
            viewModel.getAccountInfo(requireContext())
            binding.root.hideKeyboard()
        }
        viewModel.getAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingGetAccountInfoResult -> {
                    // TODO SHOW LOADING
                }
                is SuccessGetAccountInfoResult -> {
                    it.user.emailVerified?.let {verified ->
                        if (verified) {
                            viewModel.addHouseCorrection(requireContext(),HouseCorrection(0,houseId!!,it.user.localId!!,binding.editTextHouseCorrection.text.toString()))
                        }
                        else {
                            showSnackBarShort(this,binding.root,getString(R.string.email_not_verified))
                        }
                    }
                }
                is FailureGetAccountInfoResult -> {
                    when (it.errorMessage) {
                        "INVALID_ID_TOKEN" -> {
                            viewModel.refreshToken(requireContext())
                        }
                        "USER_NOT_FOUND" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                        }
                        "USER_DISABLED" -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                    }
                    viewModel.resetModel()
                }
            }
        }
        viewModel.refreshTokenResult.observe(viewLifecycleOwner) {
            when (it) {
                is SuccessRefreshTokenResult -> {
                    viewModel.getAccountInfo(requireContext())
                }
                is FailureRefreshTokenResult -> {
                    when(it.errorMessage) {
                        "TOKEN_EXPIRED" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        "USER_DISABLED" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        "USER_NOT_FOUND" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        "MISSING_REFRESH_TOKEN" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                    }
                    viewModel.resetModel()
                }
            }
        }
        viewModel.addHouseCorrectionResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingAddHouseCorrectionResult -> {
                    binding.buttonSendCorrection.isIndeterminateProgressMode = true
                    binding.buttonSendCorrection.progress = 66
                }
                is SuccessAddHouseCorrectionResult -> {
                    binding.buttonSendCorrection.progress = 0
                    showSnackBarLong(this,binding.root,getString(R.string.correction_added))
                    viewModel.resetModel()
                }
                is FailureAddHouseCorrectionResult -> {
                    binding.buttonSendCorrection.progress = 0
                    if (it.errorMessage == "POST_TIMEOUT") {
                        showSnackBarShort(this,binding.root,getString(R.string.post_timeout_correction))
                    }
                    else {
                        showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                    }
                    viewModel.resetModel()
                }
            }
        })
    }

}