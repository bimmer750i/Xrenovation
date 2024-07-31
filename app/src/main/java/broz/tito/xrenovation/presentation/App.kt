package broz.tito.xrenovation.presentation

import android.app.Application
import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.data.auth.entities.LoggedStatus
import broz.tito.xrenovation.data.auth.entities.NetworkStatus
import broz.tito.xrenovation.presentation.di.AppComponent
import broz.tito.xrenovation.presentation.di.DaggerAppComponent
import com.google.firebase.FirebaseApp
import com.yandex.mapkit.MapKitFactory

class App : Application() {

    lateinit var appComponent : AppComponent

    var loggedStatus : LoggedStatus = LoggedStatus.UNDEFINED

    var networkStatus : NetworkStatus = NetworkStatus.UNDEFINED

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

}