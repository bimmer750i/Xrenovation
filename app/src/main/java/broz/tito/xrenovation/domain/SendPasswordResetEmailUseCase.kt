package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.SendPasswordResetEmailResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(val emailRepository: broz.tito.xrenovation.domain.EmailRepository) {

    operator fun invoke(email : String) : Flow<SendPasswordResetEmailResult> {
        return emailRepository.sendPasswordResetEmail(email)
    }

}