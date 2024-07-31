package broz.tito.xrenovation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.databinding.FragmentCaptchaBinding
import broz.tito.xrenovation.presentation.interfaces.Captchable
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CaptchaFragment : Fragment(), Captchable {

    private lateinit var binding: FragmentCaptchaBinding

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