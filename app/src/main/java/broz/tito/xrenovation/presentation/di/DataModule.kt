package broz.tito.xrenovation.presentation.di

import android.content.Context
import broz.tito.xrenovation.data.auth.*
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.CaptchaRepository
import broz.tito.xrenovation.domain.EmailRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    fun provideEmailService() : AuthService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://identitytoolkit.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    fun provideCaptchaVerifyService() : CaptchaService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://smartcaptcha.yandexcloud.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(CaptchaService::class.java)
    }

    @Provides
    fun provideEmailRepository(model: AuthModel) : EmailRepository {
        return EmailRepositoryImpl(model)
    }

    @Provides
    fun provideCaptchaRepository(model: AuthModel) : CaptchaRepository {
        return CaptchaRepositoryImpl(model)
    }

    @Provides
    @Singleton
    fun provideModel(service: AuthService,captchaService: CaptchaService) : AuthModel {
        return AuthModel(service, captchaService)
    }

    @Provides
    @Singleton
    fun provideSharedPrefsModel() : SharedPrefsModel {
        return SharedPrefsModel()
    }



}