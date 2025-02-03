package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import broz.tito.xrenovation.presentation.INVALID_ID_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class AccountInfoViewModel @Inject constructor(val getAccountInfoUseCase: GetAccountInfoUseCase,
                                               val refreshTokenUseCase: RefreshTokenUseCase,
                                               val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                               val logOutUseCase: LogOutUseCase,
                                               val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
                                               val setAccountInfoUseCase: SetAccountInfoUseCase,
                                               val verifyEmailUseCase : SendEmailVerificationCodeUseCase,
                                               val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    private val _getAccountInfoResult = MutableLiveData<GetAccountInfoResult>()
    val getAccountInfoResult : LiveData<GetAccountInfoResult> = _getAccountInfoResult

    private val _uploadProfilePictureResult = MutableLiveData<UploadProfilePictureResult>()
    val uploadProfilePictureResult : LiveData<UploadProfilePictureResult> = _uploadProfilePictureResult

    private val _setAccountInfoResult = MutableLiveData<SetAccountInfoResult>()
    val setAccountInfoResult : LiveData<SetAccountInfoResult> = _setAccountInfoResult

    private val _verifyEmailResult  = MutableLiveData<VerifyEmailResult>()
    val verifyEmailResult : LiveData<VerifyEmailResult> = _verifyEmailResult

    fun logOut(context: Context) {
        logOutUseCase(context)
    }

    fun getAccountInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccountInfoUseCase(sharedPrefsModel.getIdToken(context)).onEach { accountInfoResult ->
                when (accountInfoResult) {
                    is FailureGetAccountInfoResult -> {
                        when (accountInfoResult.errorMessage) {
                            INVALID_ID_TOKEN -> {
                                val refreshTokenResult = refreshToken(context, refreshTokenUseCase, sharedPrefsModel, saveAuthResponseUseCase)
                                when (refreshTokenResult) {
                                    is SuccessRefreshTokenResult -> {
                                        val accountInfoResultAgain = broz.tito.xrenovation.presentation.models.getAccountInfo(context,getAccountInfoUseCase, sharedPrefsModel)
                                        _getAccountInfoResult.postValue(accountInfoResultAgain)
                                    }
                                    is FailureRefreshTokenResult -> {
                                        _getAccountInfoResult.postValue(FailureGetAccountInfoResult(refreshTokenResult.errorMessage))
                                    }
                                }
                            }
                            else -> {
                                _getAccountInfoResult.postValue(accountInfoResult)
                            }
                        }
                    }
                    else -> {
                        _getAccountInfoResult.postValue(accountInfoResult)
                    }
                }
            }.collect()
        }
    }

    fun uploadProfilePicture(context : Context, file : File) {
        viewModelScope.launch(Dispatchers.IO) {
            uploadProfilePictureUseCase(context, file).onEach {
                _uploadProfilePictureResult.postValue(it)
            }.collect()
        }
    }

    fun setAccountInfo(context: Context,displayName : String?,photoUrl : String?) {
        viewModelScope.launch(Dispatchers.IO) {
            setAccountInfoUseCase(sharedPrefsModel.getIdToken(context),displayName,photoUrl,null).onEach {
                _setAccountInfoResult.postValue(it)
            }.collect()
        }
    }

    fun sendEmailVerificationCode(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            verifyEmailUseCase(sharedPrefsModel.getIdToken(context)).onEach {
                _verifyEmailResult.postValue(it)
            }.collect()
        }
    }

    fun resetVerifyEmailState() {
        _verifyEmailResult.postValue(VerifyEmailResult())
    }

    fun resetGetAccountInfoState() {
        _getAccountInfoResult.postValue(GetAccountInfoResult())
    }

    fun resetUploadProfilePictureResult() {
        _uploadProfilePictureResult.postValue(UploadProfilePictureResult())
    }

    fun resetSetAccountInfoResult() {
        _setAccountInfoResult.postValue(SetAccountInfoResult())
    }

}