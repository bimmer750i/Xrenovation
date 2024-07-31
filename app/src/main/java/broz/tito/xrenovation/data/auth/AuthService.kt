package broz.tito.xrenovation.data.auth

import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.data.auth.entities.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("v1/accounts:signUp?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun signUpByEmail(@Body body: SignUpByEmailBody) : Response<RawSignUpByEmailResponse>

    @POST("v1/accounts:sendOobCode?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun sendEmailVerificationCode(@Body body: VerifyEmailBody) : Response<VerifyEmailResponse>

    @POST("v1/accounts:signInWithPassword?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun signInByEmail(@Body body : SignInByEmailBody) : Response<RawSignInByEmailResponse>

    @POST("v1/accounts:update?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun setAccountInfo(@Body body : SetAccountInfoBody) : Response<SetAccountInfoResponse>

    @POST("v1/accounts:lookup?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun getAccountInfo(@Body body: GetAccountInfoBody) : Response<GetAccountInfoResponse>

    @POST("v1/accounts:sendOobCode?key=${BuildConfig.FIREBASE_WEB_KEY}")
    suspend fun sendPasswordResetEmail(@Body body: SendPasswordResetEmailBody) : Response<SendPasswordResetEmailResponse>

}