package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentSignInBinding
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.SignInByEmailViewModel
import broz.tito.xrenovation.presentation.models.SignInByEmailViewModelFactory
import javax.inject.Inject

class SignInFragment : Fragment(), ProgressBarAble, SnackBarAble {

    private val TAG = "SignInFragment"


    private lateinit var binding: FragmentSignInBinding

    @Inject
    lateinit var viewModelFactory: SignInByEmailViewModelFactory

    lateinit var viewModel: SignInByEmailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,viewModelFactory)[SignInByEmailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ButtonSignIn.setOnClickListener {
            signInByEmail()
        }
        binding.textviewForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_enterEmailFragment)
        }
        viewModel.signInResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSignInByEmailResult -> {
                    binding.textviewForgotPassword.visibility = View.GONE
                    showProgressBar()
                }
                is SuccessSignInByEmailResult -> {
                    findNavController().navigate(R.id.action_signInFragment_to_accountInfoFragment)
                    (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_IN
                    hideProgressBar()

                }
                is FailureSignInByEmailResult -> {
                    binding.textviewForgotPassword.visibility = View.VISIBLE
                    hideProgressBar()
                    when (it.errorMessage)  {
                        "INVALID_EMAIL" -> {

                        }
                        "EMAIL_NOT_FOUND" -> {
                            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.user_not_found))
                        }
                        "INVALID_PASSWORD" -> {
                            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.invalid_password))
                        }
                        "USER_DISABLED" -> {
                            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.error_try_again))
                        }
                        "INVALID_LOGIN_CREDENTIALS" -> {
                            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.wrong_email_or_password))
                        }
                        else -> {
                            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.error_try_again))
                        }
                    }
                }
            }
        }
    }

    private fun signInByEmail() {
        if (binding.editTextEmailSignIn.text.toString().checkIfEmailCorrect() &&
                binding.editTextPasswordSignIn.text.length > 5) {
            viewModel.signInByEmail(requireContext(),binding.editTextEmailSignIn.text.toString(),binding.editTextPasswordSignIn.text.toString())
        }
        else if (binding.editTextEmailSignIn.text.isNullOrEmpty()) {
            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.email_is_empty))
        }
        else if (!binding.editTextEmailSignIn.text.toString().checkIfEmailCorrect()) {
            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.incorrect_email))
        }
        else if (binding.editTextPasswordSignIn.text.isNullOrEmpty()) {
            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.password_is_empty))
        }
        else if (binding.editTextPasswordSignIn.text.length < 6) {
            showSnackBarShort(this,binding.signInFragmentLayout,getString(R.string.invalid_password))
        }
    }

    override fun hideProgressBar() {
        binding.progressBar2.visibility = View.GONE
    }

    override fun showProgressBar() {
        binding.progressBar2.visibility = View.VISIBLE
    }

}