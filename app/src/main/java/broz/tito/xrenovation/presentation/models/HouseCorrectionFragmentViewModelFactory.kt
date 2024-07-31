package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.AddHouseCorrectionUseCase
import broz.tito.xrenovation.domain.GetAccountInfoUseCase
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import javax.inject.Inject

class HouseCorrectionFragmentViewModelFactory @Inject constructor(
    val getAccountInfoUseCase: GetAccountInfoUseCase,
    val refreshTokenUseCase: RefreshTokenUseCase,
    val saveAuthResponseUseCase: SaveAuthResponseUseCase,
    val useCase: AddHouseCorrectionUseCase,
    val sharedPrefsModel: SharedPrefsModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HouseCorrectionFragmentViewModel(getAccountInfoUseCase,refreshTokenUseCase,saveAuthResponseUseCase,useCase,sharedPrefsModel) as T
    }
}