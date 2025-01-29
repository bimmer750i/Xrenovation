package broz.tito.xrenovation.presentation.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.CaptchaResult
import broz.tito.xrenovation.data.auth.entities.FailureCaptchaResult
import broz.tito.xrenovation.data.auth.entities.FailureSignUpByEmailResult
import broz.tito.xrenovation.data.auth.entities.PendingSignUpByEmailResult
import broz.tito.xrenovation.data.auth.entities.SignUpByEmailResult
import broz.tito.xrenovation.data.auth.entities.SuccessCaptchaResult
import broz.tito.xrenovation.data.auth.entities.SuccessSignUpByEmailResult
import broz.tito.xrenovation.domain.*
import broz.tito.xrenovation.presentation.REGISTRATION_ERROR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpByEmailViewModel @Inject constructor(val useCase: SignUpByEmailUseCase, val captchaUseCase: VerifyCaptchaUseCase,
                                                 val saveAuthResponseUseCase : SaveAuthResponseUseCase) : ViewModel() {

    private val TAG = "SignUpByEmailViewModel"

    private val _signUpResult  = MutableLiveData<SignUpByEmailResult>()
    val signUpResult : LiveData<SignUpByEmailResult> = _signUpResult

    private suspend fun signUpByEmail1(context: Context,email: String,password : String)
    : SignUpByEmailResult = coroutineScope {
        async {
            useCase(email, password).onEach {
                Log.d(TAG, "signUpByEmail: result -- ${it.javaClass.simpleName}")
                if (it is SuccessSignUpByEmailResult && it.result.idToken != null && it.result.email != null && it.result.refreshToken != null && it.result.localId != null) {
                    saveAuthResponseUseCase(context, it.result.idToken, email, it.result.refreshToken, it.result.localId)
                }
            }.last()
        }.await()
    }

    private suspend fun verifyCaptcha1(serverToken : String,ip : String,captchaToken : String) : CaptchaResult = coroutineScope {
        async {
            captchaUseCase(serverToken, ip, captchaToken).last()
        }.await()
    }

    fun signUpByEmail(context: Context,email: String,password : String,serverToken : String,ip : String,captchaToken : String) {
        viewModelScope.launch(Dispatchers.IO) {
            _signUpResult.postValue(PendingSignUpByEmailResult())
            // VERIFYING CAPTCHA
            val captchaResult = verifyCaptcha1(serverToken, ip, captchaToken)
            when (captchaResult) {
                // IF SUCCESSFUL RESPONSE FROM CAPTCHA WAS RECEIVED
                is SuccessCaptchaResult -> {
                    when (captchaResult.response.status) {
                        // CAPTCHA VERIFIED, SIGNING UP BY EMAIL
                        "ok" -> {
                            val signUpByEmailResult = signUpByEmail1(context, email, password)
                            _signUpResult.postValue(signUpByEmailResult)
                        }
                        // CAPTCHA NOT VERIFIED, THE USER IS A BOT !!!
                        else -> {
                            _signUpResult.postValue(FailureSignUpByEmailResult(REGISTRATION_ERROR))
                        }
                    }
                }
                // FAILED TO VERIFY CAPTCHA
                is FailureCaptchaResult -> {
                    _signUpResult.postValue(FailureSignUpByEmailResult(REGISTRATION_ERROR))
                }
            }
        }
    }

    fun resetViewModelState() {
        _signUpResult.postValue(SignUpByEmailResult())
    }

}