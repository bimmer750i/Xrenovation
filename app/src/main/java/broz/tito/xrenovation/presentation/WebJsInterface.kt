package broz.tito.xrenovation.presentation

import android.webkit.JavascriptInterface
import broz.tito.xrenovation.presentation.interfaces.Captchable

class WebJsInterface(val captchable: Captchable) {

    private val TAG = "WebViewJS"

    @JavascriptInterface
    fun onGetToken(token: String) {
        captchable.passCaptchaToken(token)
    }
}