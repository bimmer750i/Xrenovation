package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.FailureSendPasswordResetEmailResult
import broz.tito.xrenovation.data.auth.entities.PendingSendPasswordResetEmailResult
import broz.tito.xrenovation.data.auth.entities.SuccessSendPasswordResetEmailResult
import broz.tito.xrenovation.databinding.FragmentEnterEmailBinding
import broz.tito.xrenovation.presentation.interfaces.Disablable
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.EnterEmailViewModel
import broz.tito.xrenovation.presentation.models.EnterEmailViewModelFactory
import javax.inject.Inject


class EnterEmailFragment : Fragment(), ProgressBarAble, SnackBarAble, Disablable {

    private lateinit var binding : FragmentEnterEmailBinding

    @Inject
    lateinit var enterEmailViewModelFactory: EnterEmailViewModelFactory

    private lateinit var viewModel: EnterEmailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,enterEmailViewModelFactory)[EnterEmailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterEmailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSendPasswordResetEmail.setOnClickListener {
            if (!binding.buttonSendPasswordResetEmail.isIndeterminateProgressMode) {
                sendPasswordResetEmail()
            }
        }
        viewModel.sendPasswordResetEmailResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSendPasswordResetEmailResult -> {
                    showProgressBar()
                    disableViews()
                }
                is SuccessSendPasswordResetEmailResult -> {
                    hideProgressBar()
                    enableViews()
                    showSnackBarShort(this,binding.enterEmailFragmentLayout,getString(R.string.password_reset_email_sent))
                }
                is FailureSendPasswordResetEmailResult -> {
                    hideProgressBar()
                    enableViews()
                    when(it.errorMessage) {
                        "EMAIL_NOT_FOUND" -> {
                            showSnackBarShort(this,binding.enterEmailFragmentLayout,getString(R.string.user_not_found))
                        }
                        else -> {
                            showSnackBarShort(this,binding.enterEmailFragmentLayout,getString(R.string.password_reset_email_error))
                        }
                    }

                }
            }
        }
    }

    override fun showProgressBar() {
        binding.buttonSendPasswordResetEmail.isIndeterminateProgressMode = true
        binding.buttonSendPasswordResetEmail.progress = 66
    }

    override fun hideProgressBar() {
        binding.buttonSendPasswordResetEmail.isIndeterminateProgressMode = false
        binding.buttonSendPasswordResetEmail.progress = 0
    }

    override fun enableViews() {
        binding.apply {
            editTextEmailPasswordReset.isEnabled = true
        }
    }

    override fun disableViews() {
        binding.apply {
            editTextEmailPasswordReset.isEnabled = false
        }
    }

    private fun sendPasswordResetEmail() {
        if (binding.editTextEmailPasswordReset.text.toString().checkIfEmailCorrect()) {
            viewModel.sendPasswordResetEmail(binding.editTextEmailPasswordReset.text.toString())
        }
        else {
            showSnackBarShort(this,binding.enterEmailFragmentLayout,getString(R.string.incorrect_email))
        }

    }

}