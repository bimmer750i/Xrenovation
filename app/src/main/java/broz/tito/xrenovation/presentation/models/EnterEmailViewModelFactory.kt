package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.domain.SendPasswordResetEmailUseCase
import javax.inject.Inject

class EnterEmailViewModelFactory @Inject constructor(val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EnterEmailViewModel(sendPasswordResetEmailUseCase) as T
    }
}