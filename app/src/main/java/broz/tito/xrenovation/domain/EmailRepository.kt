package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.*
import kotlinx.coroutines.flow.Flow
import java.io.File

interface EmailRepository {

    fun signUpByEmail(email : String, password : String) : Flow<SignUpByEmailResult>

    fun sendEmailVerificationCode(idToken : String) : Flow<VerifyEmailResult>

    fun signInByEmail(email: String, password: String) : Flow<SignInByEmailResult>

    fun setAccountInfo(idToken: String, displayName : String?, photoUrl : String?, deleteAttribute : ArrayList<String>?) : Flow<SetAccountInfoResult>

    fun getAccountInfo(idToken: String) : Flow<GetAccountInfoResult>

    fun refreshToken(refreshToken : String) : Flow<RefreshTokenResult>

    suspend fun uploadProfilePicture(localId : String, file: File) : Flow<UploadProfilePictureResult>

    fun sendPasswordResetEmail(email : String) : Flow<SendPasswordResetEmailResult>

}