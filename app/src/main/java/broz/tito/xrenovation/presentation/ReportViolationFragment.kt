package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.POST_TIMEOUT
import broz.tito.xrenovation.data.add_house.entities.FailureReportViolationResult
import broz.tito.xrenovation.data.add_house.entities.HouseCorrection
import broz.tito.xrenovation.data.add_house.entities.PendingReportViolationResult
import broz.tito.xrenovation.data.add_house.entities.ReportViolation
import broz.tito.xrenovation.data.add_house.entities.SuccessReportViolationResult
import broz.tito.xrenovation.data.auth.entities.LoggedStatus
import broz.tito.xrenovation.databinding.FragmentReportViolationBinding
import broz.tito.xrenovation.presentation.HouseCorrectionFragment.CorrectionLength
import broz.tito.xrenovation.presentation.interfaces.Disablable
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.ReportViolationViewModel
import broz.tito.xrenovation.presentation.models.ReportViolationViewModelFactory
import javax.inject.Inject


class ReportViolationFragment : Fragment(),Disablable,ProgressBarAble,SnackBarAble {

    val args: ReportViolationFragmentArgs by navArgs()
    private lateinit var binding: FragmentReportViolationBinding

    @Inject
    lateinit var viewModelFactory: ReportViolationViewModelFactory

    private lateinit var viewModel: ReportViolationViewModel

    private var commentId : String = ""
    private var criminalLocalId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.let {
            commentId = it.commentId
            criminalLocalId = it.criminalLocalId
        }
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,viewModelFactory)[ReportViolationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportViolationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSendViolationReport.setOnClickListener {
            if (binding.editTextReportViolation.checkReportViolationLength { reportViolationLength ->
                    when (reportViolationLength) {
                        ReportViolationLength.SHORT -> showSnackBarShort(this,binding.root,getString(R.string.correction_too_short_error))
                        ReportViolationLength.LONG -> showSnackBarShort(this,binding.root,getString(R.string.correction_too_long_error))
                    }
                }) {
                viewModel.addReportViolation(requireContext(), ReportViolation(commentId,criminalLocalId,"",binding.editTextReportViolation.text.toString()))
            }
            binding.root.hideKeyboard()
        }
        viewModel.reportViolationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is PendingReportViolationResult -> {
                    disableViews()
                    showProgressBar()
                }
                is SuccessReportViolationResult -> {
                    enableViews()
                    hideProgressBar()
                    showSnackBarShort(this,binding.root,getString(R.string.report_added))
                    viewModel.resetState()
                }
                is FailureReportViolationResult -> {
                    enableViews()
                    hideProgressBar()
                    when (result.errorMessage) {
                        USER_NOT_FOUND -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found_error))
                        }
                        USER_DISABLED -> {
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            showSnackBarShort(this,binding.root,getString(R.string.error_try_again))
                        }
                        TOKEN_EXPIRED -> {
                            showSnackBarShort(this,binding.root,getString(R.string.token_expired_error))
                        }
                        MISSING_REFRESH_TOKEN -> {
                            showSnackBarShort(this,binding.root,getString(R.string.missing_refresh_token_error))
                        }
                        NO_NETWORK -> {
                            showSnackBarShort(this,binding.root,getString(R.string.no_network_try_again))
                        }
                        POST_TIMEOUT -> {
                            showSnackBarShort(this,binding.root,getString(R.string.post_timeout_correction))
                        }
                        else -> {
                            showSnackBarShort(this,binding.root,getString(R.string.get_account_info_error))
                        }
                    }
                    viewModel.resetState()
                }
            }
        }
    }


    override fun enableViews() {
        binding.buttonSendViolationReport.isEnabled = true
        binding.editTextReportViolation.isEnabled = true
    }

    override fun disableViews() {
        binding.buttonSendViolationReport.isEnabled = true
        binding.editTextReportViolation.isEnabled = true
    }

    override fun showProgressBar() {
        binding.buttonSendViolationReport.isIndeterminateProgressMode = true
        binding.buttonSendViolationReport.progress = 66
    }

    override fun hideProgressBar() {
        binding.buttonSendViolationReport.isIndeterminateProgressMode = false
        binding.buttonSendViolationReport.progress = 0
    }

    fun EditText.checkReportViolationLength(lambda : (ReportViolationLength) -> Unit) : Boolean {
        if (this.length() < 20) {lambda(ReportViolationLength.SHORT);return false}
        else if (this.length() > 150) {lambda(ReportViolationLength.LONG);return false}
        else return true
    }

    enum class ReportViolationLength {
        SHORT,LONG
    }
}