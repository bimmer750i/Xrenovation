package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.CaptchaResult
import kotlinx.coroutines.flow.Flow

interface CaptchaRepository {

    fun verifyCaptcha(serverToken : String,ip : String,captchaToken : String) : Flow<CaptchaResult>

}