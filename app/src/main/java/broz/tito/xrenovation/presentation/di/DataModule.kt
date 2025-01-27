package broz.tito.xrenovation.presentation.di

import android.content.Context
import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.data.add_house.HouseModel
import broz.tito.xrenovation.data.add_house.AddHouseRepositoryImpl
import broz.tito.xrenovation.data.add_house.HouseService
import broz.tito.xrenovation.data.add_house.TimeService
import broz.tito.xrenovation.data.auth.*
import broz.tito.xrenovation.data.interceptors.LiveNetworkMonitor
import broz.tito.xrenovation.data.interceptors.NetworkMonitor
import broz.tito.xrenovation.data.interceptors.NetworkMonitorInterceptor
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class DataModule {

    @Provides
    fun provideNetworkMonitor(context: Context) : NetworkMonitor {
        return LiveNetworkMonitor(context)
    }

    @Provides
    fun provideOkHttpClient(interceptor: NetworkMonitorInterceptor) : OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15,TimeUnit.SECONDS)
            .connectTimeout(15,TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    fun provideEmailService(client: OkHttpClient) : AuthService {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://identitytoolkit.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    fun provideTokenService(client: OkHttpClient) : TokenService {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://securetoken.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(TokenService::class.java)

    }

    @Provides
    fun provideCaptchaVerifyService(client: OkHttpClient) : CaptchaService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://smartcaptcha.yandexcloud.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(CaptchaService::class.java)
    }

    @Provides
    fun provideAddHouseService(client: OkHttpClient) : HouseService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://${BuildConfig.PROJECT_ID}-default-rtdb.europe-west1.firebasedatabase.app")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(HouseService::class.java)
    }

    @Provides
    fun provideTimeService(client: OkHttpClient) : TimeService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://worldtimeapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(TimeService::class.java)
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
    fun provideModel(service: AuthService,captchaService: CaptchaService,tokenService: TokenService, storageReference: StorageReference) : AuthModel {
        return AuthModel(service, captchaService,tokenService,storageReference)
    }

    @Provides
    @Singleton
    fun provideSharedPrefsModel() : SharedPrefsModel {
        return SharedPrefsModel()
    }

    @Provides
    fun provideFirebaseStorage() : StorageReference {
        return Firebase.storage.reference
    }

    @Provides
    fun provideSearchManager() : SearchManager {
        return SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    }

    @Provides
    @Singleton
    fun provideAddHouseModel(searchManager: SearchManager, storageReference: StorageReference, houseService: HouseService,timeService: TimeService) : HouseModel {
        return HouseModel(searchManager,storageReference,houseService,timeService)
    }

    @Provides
    fun provideAddHouseRepository(model: HouseModel) : AddHouseRepository {
        return AddHouseRepositoryImpl(model)
    }


}