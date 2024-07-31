package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import broz.tito.xrenovation.domain.SignInByEmailUseCase
import javax.inject.Inject

class SignInByEmailViewModelFactory @Inject constructor(val useCase: SignInByEmailUseCase, val saveResponseUseCase: SaveAuthResponseUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignInByEmailViewModel(useCase,saveResponseUseCase) as T
    }
}