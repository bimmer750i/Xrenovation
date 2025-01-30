package broz.tito.xrenovation.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.databinding.FragmentAccountInfoBinding
import broz.tito.xrenovation.presentation.interfaces.ProgressBarAble
import broz.tito.xrenovation.presentation.interfaces.SnackBarAble
import broz.tito.xrenovation.presentation.models.AccountInfoViewModel
import broz.tito.xrenovation.presentation.models.AccountInfoViewModelFactory
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AccountInfoFragment : Fragment(), ProgressBarAble, SnackBarAble {

    private var isLoaded = false

    private var emailVerified = false

    private lateinit var binding : FragmentAccountInfoBinding

    @Inject
    lateinit var accountInfoViewModelFactory: AccountInfoViewModelFactory

    private lateinit var viewModel : AccountInfoViewModel

    lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            isLoaded = savedInstanceState.getBoolean(ACCOUNT_INFO_LOADED,false)
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // FUCK YOU, STUPID NAVIGATION COMPONENT
        }
        registerForActivityResult()
        (requireActivity().application as App).appComponent.inject(this)
        viewModel = ViewModelProvider(this,accountInfoViewModelFactory)[AccountInfoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonVerifyEmail.setOnClickListener {
            viewModel.sendEmailVerificationCode(requireContext())
        }
        binding.imageviewDisplayImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.swipeRefreshLayoutFragmentAccountInfo.setOnRefreshListener {
            viewModel.getAccountInfo(requireContext())
            binding.swipeRefreshLayoutFragmentAccountInfo.isRefreshing = false
        }
        binding.cardviewLogOut.setOnClickListener {
            showDialog()
        }
        viewModel.getAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingGetAccountInfoResult -> {
                    showProgressBar()
                    binding.buttonVerifyEmail.visibility = View.GONE
                }
                is SuccessGetAccountInfoResult -> {
                    isLoaded = true
                    if (it.user.displayName == null) {
                        viewModel.setAccountInfo(requireContext(),"user-${it.user.localId?.take(10)}",null)
                    }
                    else {
                        hideProgressBar()
                        binding.textviewHiSomeone.visibility = View.VISIBLE
                        binding.textviewHiSomeone.text = getString(R.string.hi_someone) + it.user.displayName
                        binding.cardviewEmailVerified.visibility = View.VISIBLE
                        binding.cardviewLogOut.visibility = View.VISIBLE
                        it.user.photoUrl?.let {url ->
                            Glide
                                .with(requireContext())
                                .load(url)
                                .centerCrop()
                                .into(binding.imageviewDisplayImage)
                        }
                        it.user.email?.let {
                            binding.textViewEmail.text = it
                        }
                        it.user.emailVerified?.let {
                            if (it) {
                                binding.imageviewEmailVerified.setImageResource(R.drawable.baseline_verified_user_24)
                                binding.textViewIsEmailVerified.text = getString(R.string.email_verified)
                            }
                            else {
                                binding.imageviewEmailVerified.setImageResource(R.drawable.baseline_cancel_24)
                                binding.textViewIsEmailVerified.text = getString(R.string.email_not_verified)
                                binding.buttonVerifyEmail.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                is FailureGetAccountInfoResult -> {
                    when(it.errorMessage) {
                        TOKEN_EXPIRED -> {
                            findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        MISSING_REFRESH_TOKEN -> {
                            findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        USER_NOT_FOUND -> {
                            hideProgressBar()
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        USER_DISABLED -> {
                            hideProgressBar()
                            (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                            findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
                        }
                        else -> {
                            hideProgressBar()
                            showSnackBarShort(this,binding.fragmentAccountInfoLayout,getString(R.string.get_account_info_error))
                        }
                    }
                }
            }
        }
        viewModel.uploadProfilePictureResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingUploadProfilePictureResult -> {
                    showProgressBar()
                }
                is SuccessUploadProfilePictureResult -> {
                    viewModel.setAccountInfo(requireContext(),null,it.url)
                }
                is FailureUploadProfilePictureResult -> {
                    hideProgressBar()
                    showSnackBarShort(this,binding.fragmentAccountInfoLayout,getString(R.string.picture_not_uploaded))
                }
            }
        }
        viewModel.setAccountInfoResult.observe(viewLifecycleOwner) {
            when (it) {
                is PendingSetAccountInfoResult -> {
                    // Nothing to do here, but keeping this
                }
                is SuccessSetAccountInfoResult -> {
                    viewModel.getAccountInfo(requireContext())
                }
                is FailureSetAccountInfoResult -> {
                    hideProgressBar()
                    showSnackBarShort(this,binding.fragmentAccountInfoLayout,getString(R.string.set_account_info_error))
                }
            }
        }

        viewModel.verifyEmailResult.observe(viewLifecycleOwner)  {
            when (it) {
                is PendingVerifyEmailResult-> {
                    binding.buttonVerifyEmail.isIndeterminateProgressMode = true
                    binding.buttonVerifyEmail.progress = 66
                }
                is SuccessVerifyEmailResult -> {
                    binding.buttonVerifyEmail.progress = 0
                    if (!emailVerified) {
                        showSnackBarShort(this,binding.root,getString(R.string.verification_email_sent))
                    }
                    emailVerified = true
                    viewModel.resetGetAccountInfoViewModelState()
                }
                is FailureVerifyEmailResult -> {
                    binding.buttonVerifyEmail.progress = 0
                    when (it.errorMessage) {
                        INVALID_ID_TOKEN -> {
                            showSnackBarShort(this,binding.root,getString(R.string.invalid_id_token))
                        }
                        USER_NOT_FOUND -> {
                            showSnackBarShort(this,binding.root,getString(R.string.user_not_found))
                        }
                        TOO_MANY_ATTEMPTS_TRY_LATER -> {
                            showSnackBarShort(this,binding.root,getString(R.string.sign_up_error_many_attempts))
                        }
                    }
                    emailVerified = false
                    viewModel.resetGetAccountInfoViewModelState()
                }
            }
        }

        if (!isLoaded) {
            viewModel.getAccountInfo(requireContext())
            isLoaded = true
        }
    }

    private fun registerForActivityResult() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val file = File(requireActivity().externalCacheDir,"avatar.jpg")
                val outputStream = FileOutputStream(file)
                val inputStream = requireActivity().contentResolver.openInputStream(uri)
                inputStream?.copyTo(outputStream)
                viewModel.uploadProfilePicture(requireContext(),file)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    fun showDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_log_out)
            .setPositiveButton(R.string.yes) { dialogInterface, num ->
                viewModel.logOut(requireContext())
                (requireActivity().application as App).loggedStatus = LoggedStatus.LOGGED_OUT
                findNavController().navigate(R.id.action_accountInfoFragment_to_accountFragment)
            }
            .setNegativeButton(R.string.no) { dialogInterface, num ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ACCOUNT_INFO_LOADED,isLoaded)
        outState.putBoolean(EMAIL_VERIFIED_KEY,emailVerified)
    }


    override fun showProgressBar() {
        binding.progressBarAccountInfo.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        binding.progressBarAccountInfo.visibility = View.GONE
    }

    companion object {
        const val ACCOUNT_INFO_LOADED = "ACCOUNT_INFO_LOADED"
        private val EMAIL_VERIFIED_KEY = "EMAIL_VERIFIED"
    }
}