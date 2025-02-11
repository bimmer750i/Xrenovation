package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.DataViolationUseCase
import broz.tito.xrenovation.domain.GetAccountInfoUseCase
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import javax.inject.Inject

class ReportViolationViewModelFactory @Inject constructor(val dataViolationUseCase: DataViolationUseCase,
                                                          val getAccountInfoUseCase: GetAccountInfoUseCase,
                                                          val refreshTokenUseCase: RefreshTokenUseCase,
                                                          val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                          val sharedPrefsModel: SharedPrefsModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReportViolationViewModel(dataViolationUseCase, getAccountInfoUseCase, refreshTokenUseCase, saveAuthResponseUseCase, sharedPrefsModel) as T
    }
}