package broz.tito.xrenovation.presentation

import android.util.Log
import android.webkit.JavascriptInterface

class WebJsInterface(val captchable: Captchable) {

    private val TAG = "WebViewJS"

    @JavascriptInterface
    fun onGetToken(token: String) {
        captchable.passCaptchaToken(token)
    }
}