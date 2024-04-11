package broz.tito.xrenovation.data.auth

import android.util.Log
import broz.tito.xrenovation.data.auth.entities.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthModel @Inject constructor(val service: AuthService, val captchaService: CaptchaService) {

    val TAG = "AuthModel"

    fun signUpByEmail(body: SignUpByEmailBody) : Flow<SignUpByEmailResult> = flow {
        var result : SignUpByEmailResult = PendingSignUpByEmailResult()
        emit(result)
        try {
            val response = service.signUpByEmail(body)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()!!.string()
                val error = Gson().fromJson(errorBody, FullFireBaseSignUpError::class.java)
                result = FailureSignUpByEmailResult(error.error.message).also {
                    Log.d(TAG,it.javaClass.simpleName + " -- " + it.errorMessage)
                }

            }
            else {
                response.body()?.let {
                    Log.d(TAG, "Successful result !")
                    result = SuccessSignUpByEmailResult(RawSignUpByEmailResponse(it.idToken,it.email,it.refreshToken,it.expiresIn,it.localId))
                }
            }
        }
        catch (e : Exception) {
            result = FailureSignUpByEmailResult("EXCEPTION_OCCURRED").also {
                Log.d(TAG, it.errorMessage)
                Log.d(TAG, e.message.toString())
            }
        }
        emit(result)
    }

    fun verifyCaptcha(serverToken : String,ip : String,captchaToken : String) : Flow<CaptchaResult> = flow {
        var result : CaptchaResult = PendingCaptchaResult()
        emit(result)
        try {
            val response = captchaService.verifyCaptcha(serverToken,ip,captchaToken)
            if (!response.isSuccessful) {
                result = FailureCaptchaResult()
            }
            else {
                response.body()?.let {
                    result = SuccessCaptchaResult(it)
                }
            }

        }
        catch (e : Exception) {
            result = FailureCaptchaResult()
        }
        emit(result)
    }

    fun sendEmailVerificationCode(idToken : String) : Flow<VerifyEmailResult> = flow {
        var result : VerifyEmailResult = PendingVerifyEmailResult()
        emit(result)
        try {
            val response = service.sendEmailVerificationCode(VerifyEmailBody(idToken))
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()!!.string()
                val error = Gson().fromJson(errorBody, FullFireBaseSignUpError::class.java)
                result = FailureVerifyEmailResult(error.error.message).also {
                    Log.d(TAG,it.javaClass.simpleName + " -- " + it.errorMessage)
                }
            }
            else {
                response.body()?.let {
                    result = SuccessVerifyEmailResult(it)
                }
            }
        }
        catch (e : Exception) {
            result = FailureVerifyEmailResult(e.message.toString())
        }
        emit(result)
    }


}