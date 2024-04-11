package broz.tito.xrenovation.presentation.di

import android.content.Context
import broz.tito.xrenovation.presentation.AccountFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [DataModule::class])
interface AppComponent {

    fun inject(accountFragment : AccountFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent

    }

}