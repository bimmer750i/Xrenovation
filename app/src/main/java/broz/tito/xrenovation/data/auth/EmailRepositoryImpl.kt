package broz.tito.xrenovation.data.auth

import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.domain.EmailRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(val model : AuthModel) : EmailRepository {

    override fun signUpByEmail(email: String, password: String): Flow<SignUpByEmailResult> {
        return model.signUpByEmail(SignUpByEmailBody(email, password,true))
    }

    override fun sendEmailVerificationCode(idToken: String): Flow<VerifyEmailResult> {
        return model.sendEmailVerificationCode(idToken)
    }

    override fun signInByEmail(email: String, password: String): Flow<SignInByEmailResult> {
        return model.signInByEmail(SignInByEmailBody(email, password))
    }

    override fun setAccountInfo(
        idToken: String,
        displayName: String?,
        photoUrl: String?,
        deleteAttribute: ArrayList<String>?
    ): Flow<SetAccountInfoResult> {
        return model.setAccountInfo(SetAccountInfoBody(idToken, displayName, photoUrl, deleteAttribute,true))
    }

    override fun getAccountInfo(idToken: String): Flow<GetAccountInfoResult> {
        return model.getAccountInfo(GetAccountInfoBody(idToken))
    }

    override fun refreshToken(refreshToken: String): Flow<RefreshTokenResult> {
        return model.refreshToken(RefreshTokenBody(refreshToken))
    }

    override suspend fun uploadProfilePicture(localId: String, file: File): Flow<UploadProfilePictureResult> {
        return model.uploadProfilePicture(localId,file)
    }

    override fun sendPasswordResetEmail(email: String): Flow<SendPasswordResetEmailResult> {
        return model.sendPasswordResetEmail(SendPasswordResetEmailBody(email))
    }
}