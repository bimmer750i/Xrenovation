package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import javax.inject.Inject

class StartFragmentViewModelFactory @Inject constructor(val useCase: RefreshTokenUseCase, val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                        val sharedPrefsModel: SharedPrefsModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StartFragmentViewModel(useCase,saveAuthResponseUseCase, sharedPrefsModel) as T
    }

}