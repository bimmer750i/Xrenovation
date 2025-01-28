package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.POST_TIMEOUT
import broz.tito.xrenovation.data.add_house.entities.FailureAddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.HouseCorrection
import broz.tito.xrenovation.data.add_house.entities.PendingAddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.SuccessAddHouseCorrectionResult
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentHouseCorrectionBinding
import broz.tito.xrenovation.presentation.interfaces.Disablable
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.HouseCorrectionFragmentViewModel
import broz.tito.xrenovation.presentation.models.HouseCorrectionFragmentViewModelFactory
import javax.inject.Inject

class HouseCorrectionFragment : Fragment(),SnackBarAble,ProgressBarAble,Disablable {

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
            if (binding.editTextHouseCorrection.checkCorrectionLength { correctionLength ->
                when (correctionLength) {
                    CorrectionLength.SHORT -> showSnackBarShort(this,binding.root,getString(R.string.correction_too_short_error))
                    CorrectionLength.LONG -> showSnackBarShort(this,binding.root,getString(R.string.correction_too_long_error))
                }
                }) {
                viewModel.getAccountInfo(requireContext())
            }
            binding.root.hideKeyboard()
        }
        viewModel.getAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingGetAccountInfoResult -> {
                    showProgressBar()
                    disableViews()
                }
                is SuccessGetAccountInfoResult -> {
                    it.user.emailVerified?.let {verified ->
                        if (verified && it.user.localId != null) {
                            viewModel.addHouseCorrection(requireContext(),HouseCorrection(0,houseId ?: "",it.user.localId,binding.editTextHouseCorrection.text.toString()))
                        }
                        else {
                            showSnackBarShort(this,binding.root,getString(R.string.email_not_verified))
                            hideProgressBar()
                            enableViews()
                            viewModel.resetModel()
                        }
                    }
                }
                is FailureGetAccountInfoResult -> {
                    when (it.errorMessage) {
                        INVALID_ID_TOKEN -> {
                            viewModel.refreshToken(requireContext())
                        }
                        USER_NOT_FOUND -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found_error))
                            hideProgressBar()
                            enableViews()
                        }
                        USER_DISABLED -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.error_try_again))
                            hideProgressBar()
                            enableViews()
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                            hideProgressBar()
                            enableViews()
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
                        TOKEN_EXPIRED -> {
                            hideProgressBar()
                            enableViews()
                            showSnackBarShort(this,binding.root,getString(R.string.token_expired_error))
                        }
                        USER_DISABLED -> {
                            hideProgressBar()
                            enableViews()
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                        USER_NOT_FOUND -> {
                            hideProgressBar()
                            enableViews()
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found_error))
                        }
                        MISSING_REFRESH_TOKEN -> {
                            hideProgressBar()
                            enableViews()
                            showSnackBarShort(this,binding.root,getString(R.string.missing_refresh_token_error))
                        }
                        else -> {
                            hideProgressBar()
                            enableViews()
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                    }
                    viewModel.resetModel()
                }
            }
        }
        viewModel.addHouseCorrectionResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is SuccessAddHouseCorrectionResult -> {
                    hideProgressBar()
                    enableViews()
                    showSnackBarLong(this,binding.root,getString(R.string.correction_added))
                    viewModel.resetModel()
                }
                is FailureAddHouseCorrectionResult -> {
                    if (it.errorMessage == POST_TIMEOUT) {
                        hideProgressBar()
                        enableViews()
                        showSnackBarShort(this,binding.root,getString(R.string.post_timeout_correction))
                    }
                    else {
                        hideProgressBar()
                        enableViews()
                        showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                    }
                    viewModel.resetModel()
                }
            }
        })
    }

    override fun showProgressBar() {
        if (!binding.buttonSendCorrection.isIndeterminateProgressMode) {
            binding.buttonSendCorrection.isIndeterminateProgressMode = true
            binding.buttonSendCorrection.progress = 66
        }
    }

    override fun hideProgressBar() {
        binding.buttonSendCorrection.progress = 0
        binding.buttonSendCorrection.isIndeterminateProgressMode = false
    }

    override fun enableViews() {
        binding.editTextHouseCorrection.isEnabled = true
    }

    override fun disableViews() {
        binding.editTextHouseCorrection.isEnabled = false
    }

    fun EditText.checkCorrectionLength(lambda : (CorrectionLength) -> Unit) : Boolean {
        if (this.length() < 20) {lambda(CorrectionLength.SHORT);return false}
        else if (this.length() > 150) {lambda(CorrectionLength.LONG);return false}
        else return true
    }

    enum class CorrectionLength {
        SHORT,LONG
    }
}