package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import javax.inject.Inject

class HouseFragmentViewModelFactory @Inject constructor(val addCommentUseCase: AddCommentUseCase,
                                                        val getCommentsUseCase: GetCommentsUseCase,
                                                        val getAccountInfoUseCase: GetAccountInfoUseCase,
                                                        val refreshTokenUseCase: RefreshTokenUseCase,
                                                        val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                        val sharedPrefsModel: SharedPrefsModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HouseFragmentViewModel(addCommentUseCase, getCommentsUseCase, getAccountInfoUseCase, refreshTokenUseCase, saveAuthResponseUseCase, sharedPrefsModel) as T
    }
}