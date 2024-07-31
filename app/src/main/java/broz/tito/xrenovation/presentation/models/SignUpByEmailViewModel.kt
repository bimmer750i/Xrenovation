package broz.tito.xrenovation.presentation.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.CaptchaResult
import broz.tito.xrenovation.data.auth.entities.SignUpByEmailResult
import broz.tito.xrenovation.data.auth.entities.SuccessSignUpByEmailResult
import broz.tito.xrenovation.data.auth.entities.VerifyEmailResult
import broz.tito.xrenovation.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpByEmailViewModel @Inject constructor(val useCase: SignUpByEmailUseCase, val captchaUseCase: VerifyCaptchaUseCase,
                                                 val sharedPrefsUseCase : SaveAuthResponseUseCase, val verifyEmailUseCase : SendEmailVerificationCodeUseCase,
                                                 val getIdTokenUseCase: GetIdTokenUseCase) : ViewModel() {

    private val TAG = "SignUpByEmailViewModel"

    private val _signUpResult  = MutableLiveData<SignUpByEmailResult>()
    val signUpResult : LiveData<SignUpByEmailResult> = _signUpResult

    private val _verifyCaptchaResult  = MutableLiveData<CaptchaResult>()
    val verifyCaptchaResult : LiveData<CaptchaResult> = _verifyCaptchaResult

    private val _verifyEmailResult  = MutableLiveData<VerifyEmailResult>()
    val verifyEmailResult : LiveData<VerifyEmailResult> = _verifyEmailResult

    fun signUpByEmail(context: Context,email: String,password : String) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase(email, password).onEach {
                Log.d(TAG, "signUpByEmail: result -- ${it.javaClass.simpleName}")
                if (it is SuccessSignUpByEmailResult) {
                    saveSignUpResponseInfo(context,it.result.idToken,it.result.email,it.result.refreshToken,it.result.localId)
                }
                _signUpResult.postValue(it)
            }.collect()
        }
    }

    fun verifyCaptcha(serverToken : String,ip : String,captchaToken : String) {
        viewModelScope.launch(Dispatchers.IO) {
            captchaUseCase(serverToken, ip, captchaToken).onEach {
                Log.d(TAG, "verifyCaptcha: result -- ${it.javaClass.simpleName}")
                _verifyCaptchaResult.postValue(it)
            }.collect()
        }
    }

    private fun saveSignUpResponseInfo(context: Context, idToken : String, email : String, refreshToken : String, localId : String) {
        sharedPrefsUseCase(context, idToken, email, refreshToken, localId)
        Log.d(TAG, "Info saved -- $email -- idToken: ${idToken.subSequence(0,4)} -- localId: ${localId.subSequence(0,4)}")
    }

    fun sendEmailVerificationCode(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            verifyEmailUseCase(idToken).onEach {
                _verifyEmailResult.postValue(it)
            }.collect()
        }
    }

    fun getIdToken(context: Context) : String {
        return getIdTokenUseCase(context)
    }

    fun resetViewModelState() {
        _signUpResult.postValue(SignUpByEmailResult())
        _verifyCaptchaResult.postValue(CaptchaResult())
        _verifyEmailResult.postValue(VerifyEmailResult())
    }

}