package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.SignUpByEmailResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpByEmailUseCase @Inject constructor(val repository: broz.tito.xrenovation.domain.EmailRepository) {

    operator fun invoke(email: String, password : String) : Flow<SignUpByEmailResult> {
        return repository.signUpByEmail(email, password)
    }

}