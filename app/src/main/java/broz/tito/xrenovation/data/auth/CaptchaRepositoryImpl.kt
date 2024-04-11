package broz.tito.xrenovation.data.auth

import broz.tito.xrenovation.data.auth.entities.CaptchaResult
import broz.tito.xrenovation.domain.CaptchaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CaptchaRepositoryImpl @Inject constructor(val model: AuthModel) : CaptchaRepository {

    override fun verifyCaptcha(
        serverToken: String,
        ip: String,
        captchaToken: String
    ): Flow<CaptchaResult> {
        return model.verifyCaptcha(serverToken, ip, captchaToken)
    }
}