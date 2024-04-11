package broz.tito.xrenovation.presentation

import android.app.Application
import broz.tito.xrenovation.BuildConfig
import broz.tito.xrenovation.presentation.di.AppComponent
import broz.tito.xrenovation.presentation.di.DaggerAppComponent
import com.yandex.mapkit.MapKitFactory

class App : Application() {

    lateinit var appComponent : AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
    }

}