package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.SignUpByEmailResult
import broz.tito.xrenovation.data.auth.entities.VerifyEmailResult
import kotlinx.coroutines.flow.Flow

interface EmailRepository {

    fun signUpByEmail(email : String, password : String) : Flow<SignUpByEmailResult>

    fun sendEmailVerificationCode(idToken : String) : Flow<VerifyEmailResult>

}