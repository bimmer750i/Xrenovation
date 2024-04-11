package broz.tito.xrenovation.data.auth

import broz.tito.xrenovation.data.auth.entities.SignUpByEmailResult
import broz.tito.xrenovation.data.auth.entities.VerifyEmailResult
import broz.tito.xrenovation.domain.EmailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(val model : AuthModel) : EmailRepository {

    override fun signUpByEmail(email: String, password: String): Flow<SignUpByEmailResult> {
        return model.signUpByEmail(SignUpByEmailBody(email, password,true))
    }

    override fun sendEmailVerificationCode(idToken: String): Flow<VerifyEmailResult> {
        return model.sendEmailVerificationCode(idToken)
    }
}