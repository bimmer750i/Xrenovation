package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.R
import broz.tito.xrenovation.databinding.FragmentCaptchaBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CaptchaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CaptchaFragment : Fragment(),Captchable {

    private lateinit var binding: FragmentCaptchaBinding

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCaptchaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.captchaWebView.settings.javaScriptEnabled = true
        binding.captchaWebView.addJavascriptInterface(WebJsInterface(this),"NativeClient")
        binding.captchaWebView.loadUrl("https://smartcaptcha.yandexcloud.net/webview?sitekey=${BuildConfig.CAPTCHA_KEY}")
    }

    override fun passCaptchaToken(token: String) {
        parentFragmentManager.setFragmentResult(CAPTCHA_TOKEN_CODE, bundleOf(CAPTCHA_TOKEN_VALUE to token))
        MainScope().launch {
            findNavController().navigateUp()
        }
    }

    companion object {
       const val CAPTCHA_TOKEN_CODE = "CAPTCHA_TOKEN_CODE"
        const val CAPTCHA_TOKEN_VALUE = "CAPTCHA_TOKEN_VALUE"
    }
}