package broz.tito.xrenovation.data.auth

import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.data.auth.entities.RefreshTokenBody
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenService {

    @POST("v1/token?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun refreshToken(@Body body: RefreshTokenBody) : Response<RefreshTokenResponse>

}