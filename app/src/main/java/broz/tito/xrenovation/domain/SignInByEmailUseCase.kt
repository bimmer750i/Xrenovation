package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.SignInByEmailResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInByEmailUseCase @Inject constructor(val emailRepository: broz.tito.xrenovation.domain.EmailRepository) {

    operator fun invoke(email : String, password : String) : Flow<SignInByEmailResult> {
        return emailRepository.signInByEmail(email, password)
    }

}