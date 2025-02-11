package broz.tito.xrenovation.presentation.di

import android.content.Context
import broz.tito.xrenovation.presentation.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [DataModule::class])
interface AppComponent {

    fun inject(accountFragment : AccountFragment)

    fun inject(signInFragment: SignInFragment)

    fun inject(enterNameFragment: EnterNameFragment)

    fun inject(accountInfoFragment: AccountInfoFragment)

    fun inject(startFragment: StartFragment)

    fun inject(enterEmailFragment: EnterEmailFragment)

    fun inject(addHouseFragment: AddHouseFragment)

    fun inject(findHouseOnMapFragment: FindHouseOnMapFragment)

    fun inject(mapFragment: MapFragment)

    fun inject(houseFragment : HouseFragment)

    fun inject(houseCorrectionFragment: HouseCorrectionFragment)

    fun inject(welcomeFragment: WelcomeFragment)

    fun inject(reportViolationFragment: ReportViolationFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent

    }

}