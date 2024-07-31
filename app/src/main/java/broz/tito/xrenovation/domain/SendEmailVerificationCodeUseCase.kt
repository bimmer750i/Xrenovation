package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.VerifyEmailResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendEmailVerificationCodeUseCase @Inject constructor(val repository: broz.tito.xrenovation.domain.EmailRepository) {

    operator fun invoke(idToken : String) : Flow<VerifyEmailResult> {
        return repository.sendEmailVerificationCode(idToken)
    }

}