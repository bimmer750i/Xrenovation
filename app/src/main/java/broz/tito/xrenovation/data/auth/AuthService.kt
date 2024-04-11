package broz.tito.xrenovation.data.auth

import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.data.auth.entities.RawSignUpByEmailResponse
import broz.tito.xrenovation.data.auth.entities.VerifyEmailBody
import broz.tito.xrenovation.data.auth.entities.VerifyEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("v1/accounts:signUp?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun signUpByEmail(@Body body: SignUpByEmailBody) : Response<RawSignUpByEmailResponse>

    @POST("v1/accounts:sendOobCode?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun sendEmailVerificationCode(@Body body: VerifyEmailBody) : Response<VerifyEmailResponse>

}