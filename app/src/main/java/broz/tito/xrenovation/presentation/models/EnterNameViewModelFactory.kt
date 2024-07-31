package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import broz.tito.xrenovation.domain.SetAccountInfoUseCase
import javax.inject.Inject

class EnterNameViewModelFactory @Inject constructor(val setAccountInfoUseCase: SetAccountInfoUseCase,
                                                    val refreshTokenUseCase: RefreshTokenUseCase,
                                                    val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                    val sharedPrefsModel: SharedPrefsModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EnterNameViewModel(setAccountInfoUseCase, refreshTokenUseCase, saveAuthResponseUseCase, sharedPrefsModel) as T
    }
}