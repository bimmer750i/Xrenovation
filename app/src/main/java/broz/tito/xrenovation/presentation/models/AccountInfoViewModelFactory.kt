package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import javax.inject.Inject

class AccountInfoViewModelFactory @Inject constructor(val useCase: GetAccountInfoUseCase,
                                                      val refreshTokenUseCase: RefreshTokenUseCase,
                                                      val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                      val logOutUseCase: LogOutUseCase,
                                                      val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
                                                      val setAccountInfoUseCase: SetAccountInfoUseCase,
                                                      val verifyEmailUseCase : SendEmailVerificationCodeUseCase,
                                                      val dataDeletionRequestUseCase: DataDeletionRequestUseCase,
                                                      val sharedPrefsModel: SharedPrefsModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountInfoViewModel(useCase,refreshTokenUseCase,saveAuthResponseUseCase,logOutUseCase,uploadProfilePictureUseCase,setAccountInfoUseCase,verifyEmailUseCase,dataDeletionRequestUseCase,sharedPrefsModel) as T
    }
}