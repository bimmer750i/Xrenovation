package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.domain.*
import javax.inject.Inject

class SignUpByEmailViewModelFactory @Inject constructor(val useCase: SignUpByEmailUseCase, val captchaUseCase: VerifyCaptchaUseCase,
                                                        val saveAuthResponseUseCase: SaveAuthResponseUseCase, val verifyEmailUseCase : SendEmailVerificationCodeUseCase,
                                                        val getIdTokenUseCase: GetIdTokenUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SignUpByEmailViewModel(useCase,captchaUseCase,saveAuthResponseUseCase,verifyEmailUseCase,getIdTokenUseCase) as T
    }

}