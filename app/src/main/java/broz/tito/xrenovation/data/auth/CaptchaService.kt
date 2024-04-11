package broz.tito.xrenovation.data.auth

import broz.tito.xrenovation.data.auth.entities.CaptchaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CaptchaService {

    @GET("validate")
    suspend fun verifyCaptcha(@Query("secret") serverToken : String, @Query("ip") ip : String, @Query("token") captchaToken : String) : Response<CaptchaResponse>

}