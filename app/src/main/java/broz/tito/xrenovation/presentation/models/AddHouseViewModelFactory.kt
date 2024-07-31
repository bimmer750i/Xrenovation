package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import javax.inject.Inject

class AddHouseViewModelFactory @Inject constructor(
    val getAccountInfoUseCase: GetAccountInfoUseCase,
    val refreshTokenUseCase: RefreshTokenUseCase,
    val saveAuthResponseUseCase: SaveAuthResponseUseCase,
    val sharedPrefsModel: SharedPrefsModel,
    val suggestAddressUseCase: SuggestAddressUseCase,
    val searchPointUseCase: SearchPointUseCase,
    val loadPhotosToFireBaseUseCase: LoadPhotosToFireBaseUseCase,
    val addHouseUseCase: AddHouseUseCase,
    val addHousePointUseCase: AddHousePointUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return return AddHouseViewModel(getAccountInfoUseCase,refreshTokenUseCase, saveAuthResponseUseCase, sharedPrefsModel, suggestAddressUseCase,searchPointUseCase,loadPhotosToFireBaseUseCase,addHouseUseCase,addHousePointUseCase) as T
    }
}