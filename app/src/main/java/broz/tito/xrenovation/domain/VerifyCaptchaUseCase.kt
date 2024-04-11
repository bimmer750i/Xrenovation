package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.CaptchaResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyCaptchaUseCase @Inject constructor(val repository: CaptchaRepository) {

    operator fun invoke(serverToken : String,ip : String,captchaToken : String) : Flow<CaptchaResult> {
        return repository.verifyCaptcha(serverToken, ip, captchaToken)
    }

}