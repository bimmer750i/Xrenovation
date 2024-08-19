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
import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentAccountBinding
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.SignUpByEmailViewModel
import broz.tito.xrenovation.presentation.models.SignUpByEmailViewModelFactory
import javax.inject.Inject

class AccountFragment : Fragment(), ProgressBarAble, SnackBarAble {

    private val TAG = "AccountFragment"

    @Inject
    lateinit var signUpByEmailViewModelFactory : SignUpByEmailViewModelFactory

    private lateinit var viewModel: SignUpByEmailViewModel
    private lateinit var binding: FragmentAccountBinding

    private var captchaVerified =  false
    private var signUpDone = false
    private var emailVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            captchaVerified = savedInstanceState.getBoolean(CAPTCHA_VERIFIED_KEY,false)
            signUpDone = savedInstanceState.getBoolean(SIGN_UP_DONE_KEY,false)
            emailVerified = savedInstanceState.getBoolean(EMAIL_VERIFIED_KEY,false)
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // FUCK YOU, STUPID NAVIGATION COMPONENT X2
        }
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,signUpByEmailViewModelFactory)[SignUpByEmailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        binding.buttonSignUpByEmail.setOnClickListener {
            signUpByEmail()
        }
        binding.textViewAlreadySignedUp.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_signInFragment)
        }
        parentFragmentManager.setFragmentResultListener(CaptchaFragment.CAPTCHA_TOKEN_CODE,this) { result,data ->
            val token = data.getString(CaptchaFragment.CAPTCHA_TOKEN_VALUE,"")
            if (token.isNotEmpty() && !captchaVerified) {
                hideTextView()
                viewModel.verifyCaptcha(BuildConfig.CAPTCHA_SERVER_KEY,"0.0.0.0",token)
            }
        }
        viewModel.verifyCaptchaResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingCaptchaResult -> {
                    setEditTextEnabled(false)
                    showProgressBar()
                }
                is SuccessCaptchaResult -> {
                    hideProgressBar()
                    setEditTextEnabled(true)
                    if (it.response.status == "ok" && !signUpDone) {
                        captchaVerified = true
                        viewModel.signUpByEmail(requireContext(),binding.editTextEmailSignUp.text.toString(),binding.editTextPasswordSignUp.text.toString())
                    }
                    else if (it.response.status != "ok" && !signUpDone) {
                        captchaVerified = true
                        showSnackBarShort(this,binding.root,getString(R.string.sign_up_error))
                    }
                }
                is FailureCaptchaResult -> {
                    setEditTextEnabled(true)
                    hideProgressBar()
                    showSnackBarShort(this,binding.root,getString(R.string.sign_up_error))
                    captchaVerified = false
                    showTextView()
                }
            }
        }

        viewModel.signUpResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSignUpByEmailResult -> {
                    showProgressBar()
                    setEditTextEnabled(false)
                }
                is SuccessSignUpByEmailResult -> {
                    hideProgressBar()
                    setEditTextEnabled(true)
                    signUpDone = true
                    if (!emailVerified) {
                        viewModel.sendEmailVerificationCode(viewModel.getIdToken(requireContext()))
                        Log.d(TAG, "Trying to verify email with token: ${viewModel.getIdToken(requireContext())}")
                    }

                }
                is FailureSignUpByEmailResult -> {
                    showTextView()
                    hideProgressBar()
                    setEditTextEnabled(true)
                    when (it.errorMessage) {
                        "EMAIL_EXISTS" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.sign_up_error_email_exists))
                        }
                        "TOO_MANY_ATTEMPTS_TRY_LATER" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.sign_up_error_many_attempts))
                        }
                        "EXCEPTION_OCCURRED" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.sign_up_exception))
                        }
                    }
                    signUpDone = false
                }
            }
        }
        viewModel.verifyEmailResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingVerifyEmailResult-> {
                    setEditTextEnabled(false)
                    Log.d(TAG, it.javaClass.simpleName)
                }
                is SuccessVerifyEmailResult -> {
                    setEditTextEnabled(true)
                    if (!emailVerified) {
                        showSnackBarShort(this,binding.root,getString(R.string.verification_email_sent))
                    }
                    emailVerified = true
                    findNavController().navigate(R.id.action_accountFragment_to_enterNameFragment)
                }
                is FailureVerifyEmailResult -> {
                    showTextView()
                    setEditTextEnabled(true)
                    when (it.errorMessage) {
                        "INVALID_ID_TOKEN" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.invalid_id_token))
                        }
                        "USER_NOT_FOUND" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found))
                        }
                        "TOO_MANY_ATTEMPTS_TRY_LATER" -> {
                            showSnackBarShort(this,binding.root,getString(R.string.sign_up_error_many_attempts))
                        }
                    }
                    emailVerified = false
                }

            }
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CAPTCHA_VERIFIED_KEY,captchaVerified)
        outState.putBoolean(SIGN_UP_DONE_KEY,signUpDone)
        outState.putBoolean(EMAIL_VERIFIED_KEY,emailVerified)
    }

    override fun showProgressBar() {
        binding.buttonSignUpByEmail.isIndeterminateProgressMode = true
        binding.buttonSignUpByEmail.progress = 66
    }

    override fun hideProgressBar() {
        binding.buttonSignUpByEmail.progress = 0
    }

    private fun signUpByEmail() {
        resetState()
        viewModel.resetViewModelState()
        if (binding.editTextEmailSignUp.text.toString().checkIfEmailCorrect() && checkIfPasswordsAreSame() && isPasswordStrong()) {
            hideTextView()
            findNavController().navigate(R.id.action_accountFragment_to_captchaFragment2)
        }
        else if (binding.editTextEmailSignUp.text.isNullOrEmpty()) {
            showSnackBarShort(this,binding.root,getString(R.string.email_is_empty))
        }
        else if (!binding.editTextEmailSignUp.text.toString().checkIfEmailCorrect()) {
            showSnackBarShort(this,binding.root,getString(R.string.incorrect_email))
        }
        else if (binding.editTextPasswordSignUp.text.isNullOrEmpty()) {
            showSnackBarShort(this,binding.root,getString(R.string.password_is_empty))
        }
        else if (binding.editTextPasswordConfirmSignUp.text.isNullOrEmpty()) {
            showSnackBarShort(this,binding.root,getString(R.string.confirm_password))
        }
        else if (!checkIfPasswordsAreSame()) {
            showSnackBarShort(this,binding.root,getString(R.string.different_passwords))
        }
        else if (!isPasswordStrong()) {
            showSnackBarShort(this,binding.root,getString(R.string.weak_password))
        }
    }

    private fun resetState() {
        captchaVerified =  false
        signUpDone = false
        emailVerified = false
    }

    private fun checkIfPasswordsAreSame() : Boolean {
        return binding.editTextPasswordSignUp.text.toString() == binding.editTextPasswordConfirmSignUp.text.toString()
    }

    private fun isPasswordStrong() : Boolean {
        return binding.editTextPasswordSignUp.text.length > 5
    }

    private fun hideTextView() {
        binding.textViewAlreadySignedUp.visibility = View.GONE
    }

    private fun showTextView() {
        binding.textViewAlreadySignedUp.visibility = View.VISIBLE
    }

    private fun setEditTextEnabled(bool : Boolean) {
        if (bool) {
            binding.editTextEmailSignUp.isEnabled = true
            binding.editTextPasswordSignUp.isEnabled = true
            binding.editTextPasswordConfirmSignUp.isEnabled = true
        }
        else {
            binding.editTextEmailSignUp.isEnabled = false
            binding.editTextPasswordSignUp.isEnabled = false
            binding.editTextPasswordConfirmSignUp.isEnabled = false
        }
    }

    companion object {
        private val CAPTCHA_VERIFIED_KEY = "CAPTCHA_VERIFIED"
        private val SIGN_UP_DONE_KEY = "SIGN_UP_DONE"
        private val EMAIL_VERIFIED_KEY = "EMAIL_VERIFIED"
    }


}