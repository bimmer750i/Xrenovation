package broz.tito.xrenovation.data.auth

import android.net.Uri
import android.util.Log
import broz.tito.xrenovation.data.auth.entities.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.TaskState
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.taskState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.File
import javax.inject.Inject

const val AUTH_ERROR = "AUTH_ERROR"

class AuthModel @Inject constructor(val service: AuthService, val captchaService: CaptchaService, val tokenService: TokenService, val storageReference : StorageReference) {

    val TAG = "AuthModel"

    fun signUpByEmail(body: SignUpByEmailBody) : Flow<SignUpByEmailResult> = flow {
        var result : SignUpByEmailResult = PendingSignUpByEmailResult()
        emit(result)
        try {
            val response = service.signUpByEmail(body)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()!!.string()
                val error = Gson().fromJson(errorBody, FullFireBaseSignUpError::class.java)
                result = FailureSignUpByEmailResult(error.error.message ?: AUTH_ERROR).also {
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
            result = FailureSignUpByEmailResult(e.message.toString()).also {
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
                result = FailureCaptchaResult("CAPTCHA NOT VERIFIED")
            }
            else {
                response.body()?.let {
                    result = SuccessCaptchaResult(it)
                }
            }

        }
        catch (e : Exception) {
            result = FailureCaptchaResult(e.message.toString())
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
                result = FailureVerifyEmailResult(error.error.message ?: AUTH_ERROR).also {
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

    fun signInByEmail(body: SignInByEmailBody) : Flow<SignInByEmailResult> = flow {
        var result : SignInByEmailResult = PendingSignInByEmailResult()
        emit(result)
        try {
            val response = service.signInByEmail(body)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()!!.string()
                val error = Gson().fromJson(errorBody,FullFireBaseSignUpError::class.java)
                result = FailureSignInByEmailResult(error.error.message ?: AUTH_ERROR)
                Log.d(TAG, "signInByEmail -- error -- ${error.error.message}")
            }
            else {
                response.body()?.let {
                    Log.d(TAG, "signInByEmail -- success")
                    result = SuccessSignInByEmailResult(it)
                }

            }

        }
        catch (e : Exception) {
            result = FailureSignInByEmailResult(e.message.toString())
        }
        emit(result)
    }

    fun setAccountInfo(body: SetAccountInfoBody) : Flow<SetAccountInfoResult> = flow {
        var result : SetAccountInfoResult = PendingSetAccountInfoResult()
        emit(result)
        try {
            val response = service.setAccountInfo(body)
            if (!response.isSuccessful) {
                val error = Gson().fromJson(response.errorBody()?.string(),FullFireBaseSignUpError::class.java)
                result = FailureSetAccountInfoResult(error.error.message ?: AUTH_ERROR)
                Log.d(TAG, "setAccountInfo -- failure -- ${error.error.message}")
            }
            else {
                response.body()?.let {
                    Log.d(TAG, "setAccountInfo -- success -- photo: ${it.photoUrl}")
                    result = SuccessSetAccountInfoResult(it)
                }
            }
        }
        catch (e : Exception) {
            result = FailureSetAccountInfoResult(e.message.toString())
            Log.d(TAG, "setAccountInfo -- failure -- ${e.message}")
        }
        emit(result)
    }

    fun getAccountInfo(body: GetAccountInfoBody) : Flow<GetAccountInfoResult> = flow {
        var result : GetAccountInfoResult = PendingGetAccountInfoResult()
        emit(result)
        try {
            val response = service.getAccountInfo(body)
            if (!response.isSuccessful) {
                val error = Gson().fromJson(response.errorBody()?.string(),FullFireBaseSignUpError::class.java)
                result = FailureGetAccountInfoResult(error.error.message ?: AUTH_ERROR)
                Log.d(TAG, "getAccountInfo -- error -- ${error.error.message ?: AUTH_ERROR}")
            }
            else {
                response.body()?.let {
                    if (it.users != null && it.users.size > 0) {
                        Log.d(TAG, "getAccountInfo -- user0 -- ${it.users.get(0).email} -- photo ${it.users.get(0).photoUrl}")
                        result = SuccessGetAccountInfoResult(it.users.get(0))
                    }
                }
            }
        }
        catch (e : Exception) {
            Log.d(TAG, "getAccountInfo -- exception -- ${e.message}")
            result = FailureGetAccountInfoResult(e.message.toString())
        }
        emit(result)
    }

    fun refreshToken(body: RefreshTokenBody) : Flow<RefreshTokenResult> = flow {
        var result : RefreshTokenResult = PendingRefreshTokenResult()
        emit(result)
        try {
            val response = tokenService.refreshToken(body)
            if (!response.isSuccessful) {
                val error = Gson().fromJson(response.errorBody()?.string(),FullFireBaseSignUpError::class.java)
                result = FailureRefreshTokenResult(error.error.message ?: AUTH_ERROR)
                Log.d(TAG, "refreshToken -- error -- ${error.error.message ?: AUTH_ERROR}")
            }
            else {
                response.body()?.let {
                    Log.d(TAG, "refresh -- success -- ${it.idToken} -- ${it.refreshToken}")
                    result = SuccessRefreshTokenResult(it)
                }
            }
        }
        catch (e : Exception) {
            Log.d(TAG, "refreshToken -- exception -- ${e.message}")
            result = FailureRefreshTokenResult(e.message.toString())
        }
        emit(result)
    }

    fun uploadProfilePicture(localId : String, file : File) : Flow<UploadProfilePictureResult> = callbackFlow<UploadProfilePictureResult> {
        var task : StorageTask<UploadTask.TaskSnapshot>? = null
        try {
            var result : UploadProfilePictureResult = PendingUploadProfilePictureResult()
            trySend(result)
            Log.d(TAG, "uploadProfilePicture -- pending !!!")
            val child = storageReference.child("$localId/avatars/avatar.jpg")
            task  = child.putFile(Uri.fromFile(file)).addOnSuccessListener {
                if (it.task.isSuccessful) {
                    child.downloadUrl.addOnSuccessListener {
                        result = SuccessUploadProfilePictureResult(it.toString())
                        Log.d(TAG, "uploadProfilePicture -- success $it")
                        trySend(result)
                    }
                        .addOnFailureListener {
                            result = FailureUploadProfilePictureResult(it.message.toString())
                            trySend(result)
                            Log.d(TAG, "uploadProfilePicture -- error -- ${it.message}")
                        }
                }
                else {
                    it.task.result.error?.let {exception ->
                        Log.d(TAG, "uploadProfilePicture -- failure ${exception.message.toString()}")
                        result = FailureUploadProfilePictureResult(exception.message.toString())
                        trySend(result)
                    }
                }
            }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "uploadProfilePicture -- failure ${exception.message.toString()}")
                    result = FailureUploadProfilePictureResult(exception.message.toString())
                    trySend(result)
                }
        }
        catch (e : Exception) {
            trySend(FailureUploadProfilePictureResult(e.message.toString()))
        }
        awaitClose {
            task?.removeOnSuccessListener{}
            task?.removeOnFailureListener{}
        }
    }.flowOn(Dispatchers.IO)

    fun sendPasswordResetEmail(body: SendPasswordResetEmailBody) : Flow<SendPasswordResetEmailResult> = flow {
        var result : SendPasswordResetEmailResult = PendingSendPasswordResetEmailResult()
        emit(result)
        try {
            val response = service.sendPasswordResetEmail(body)
            if (!response.isSuccessful) {
                val error = Gson().fromJson(response.errorBody()?.string(),FullFireBaseSignUpError::class.java)
                result = FailureSendPasswordResetEmailResult(error.error.message ?: AUTH_ERROR)
                Log.d(TAG, "sendPasswordResetEmail -- error -- ${error.error.message ?: AUTH_ERROR}")
            }
            else {
                response.body()?.let {
                    it.email?.let {
                        Log.d(TAG, "sendPasswordResetEmail -- success -- $it")
                        result = SuccessSendPasswordResetEmailResult(it)
                    }
                }
            }
        }
        catch (e : Exception) {
            result = FailureSendPasswordResetEmailResult(e.message.toString())
        }
        emit(result)
    }


}