package broz.tito.xrenovation.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentAccountBinding
import broz.tito.xrenovation.presentation.models.SignUpByEmailViewModel
import broz.tito.xrenovation.presentation.models.SignUpByEmailViewModelFactory
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountFragment : Fragment() {

    // TODO MAKE ALL TOASTS & CHECK FAILURES

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
        parentFragmentManager.setFragmentResultListener(CaptchaFragment.CAPTCHA_TOKEN_CODE,this) { result,data ->
            val token = data.getString(CaptchaFragment.CAPTCHA_TOKEN_VALUE,"")
            if (token.isNotEmpty() && !captchaVerified) {
                viewModel.verifyCaptcha(BuildConfig.CAPTCHA_SERVER_KEY,"0.0.0.0",token)
            }
        }
        viewModel.verifyCaptchaResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingCaptchaResult -> {
                    setEditTextEnabled(false)
                    binding.signUpProgressbar.visibility = View.VISIBLE
                }
                is SuccessCaptchaResult -> {
                    binding.signUpProgressbar.visibility = View.GONE
                    setEditTextEnabled(true)
                    if (it.response.status == "ok" && !signUpDone) {
                        captchaVerified = true
                        viewModel.signUpByEmail(requireContext(),binding.editTextEmailSignUp.text.toString(),binding.editTextPasswordSignUp.text.toString())
                    }
                    else if (it.response.status != "ok" && !signUpDone) {
                        captchaVerified = true
                        showSnackBar(getString(R.string.sign_up_error))
                    }

                }
                is FailureCaptchaResult -> {
                    setEditTextEnabled(true)
                    binding.signUpProgressbar.visibility = View.GONE
                    showSnackBar(getString(R.string.sign_up_error))
                    captchaVerified = false
                }
            }
        })
        viewModel.signUpResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingSignUpByEmailResult -> {
                    binding.signUpProgressbar.visibility = View.VISIBLE
                    setEditTextEnabled(false)
                }
                is SuccessSignUpByEmailResult -> {
                    binding.signUpProgressbar.visibility = View.GONE
                    Log.d(TAG, "${it.result.email} -- idToken: ${it.result.idToken.subSequence(0,4)} -- localId: ${it.result.localId.subSequence(0,4)}")
                    setEditTextEnabled(true)
                    signUpDone = true
                    if (!emailVerified) {
                        viewModel.sendEmailVerificationCode(viewModel.getIdToken(requireContext()))
                        Log.d(TAG, "Trying to verify email with token: ${viewModel.getIdToken(requireContext())}")
                    }

                }
                is FailureSignUpByEmailResult -> {
                    binding.signUpProgressbar.visibility = View.GONE
                    setEditTextEnabled(true)
                    when (it.errorMessage) {
                        "EMAIL_EXISTS" -> {
                            showSnackBar(getString(R.string.sign_up_error_email_exists))
                        }
                        "TOO_MANY_ATTEMPTS_TRY_LATER" -> {
                            showSnackBar(getString(R.string.sign_up_error_many_attempts))
                        }
                        "EXCEPTION_OCCURRED" -> {
                            showSnackBar(getString(R.string.sign_up_exception))
                        }
                    }
                    signUpDone = false
                }
            }
        })
        viewModel.verifyEmailResult.observe(viewLifecycleOwner, Observer {
            when (it) {
                is PendingVerifyEmailResult-> {
                    setEditTextEnabled(false)
                    Log.d(TAG, it.javaClass.simpleName)
                }
                is SuccessVerifyEmailResult -> {
                    setEditTextEnabled(true)
                    if (!emailVerified) {
                        showSnackBar(getString(R.string.verification_email_sent))
                    }
                    emailVerified = true
                }
                is FailureVerifyEmailResult -> {
                    setEditTextEnabled(true)
                    Log.d(TAG, it.javaClass.simpleName)
                    when (it.errorMessage) {
                        "INVALID_ID_TOKEN" -> {
                            showSnackBar(getString(R.string.invalid_id_token))
                        }
                        "USER_NOT_FOUND" -> {
                            showSnackBar(getString(R.string.user_not_found))
                        }
                        "TOO_MANY_ATTEMPTS_TRY_LATER" -> {
                            showSnackBar(getString(R.string.sign_up_error_many_attempts))
                        }
                    }
                    emailVerified = false
                }

            }
        })


        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CAPTCHA_VERIFIED_KEY,captchaVerified)
        outState.putBoolean(SIGN_UP_DONE_KEY,signUpDone)
        outState.putBoolean(EMAIL_VERIFIED_KEY,emailVerified)
    }



    fun showSnackBar(text : String) {
        Snackbar.make(requireContext(),binding.accountFragmentLayout,text,Snackbar.LENGTH_SHORT).show()
    }

    fun signUpByEmail() {
        resetState()
        viewModel.resetViewModelState()
        if (checkIfEmailCorrect(binding.editTextEmailSignUp.text.toString()) && checkIfPasswordsAreSame() && isPasswordStrong()) {
            findNavController().navigate(R.id.action_accountFragment_to_captchaFragment2)
        }
        else if (!checkIfEmailCorrect(binding.editTextEmailSignUp.text.toString())) {
            showSnackBar(getString(R.string.incorrect_email))
        }
        else if (!checkIfPasswordsAreSame()) {
            showSnackBar(getString(R.string.different_passwords))
        }
        else if (!isPasswordStrong()) {
            showSnackBar(getString(R.string.weak_password))
        }
    }

    fun checkIfEmailCorrect(email : String) : Boolean {
        if (!email.contains("@")) {
           return false
        }
        else if (email.indexOf("@") == 0) {
            return false
        }
        else if (email.indexOf("@") == email.length - 1) {
            return false
        }
        else if (!email.contains(".")) {
            return false
        }
        else if (email.drop(email.indexOf("@") + 1).indexOf(".") < 1) {
            return false
        }
        else if (email.drop(email.indexOf(".") + 1).length < 2) {
            return false
        }
        else {
            return true
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