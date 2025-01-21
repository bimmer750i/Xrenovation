package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.*
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class AccountInfoViewModel @Inject constructor(val useCase: GetAccountInfoUseCase,
                                               val refreshTokenUseCase: RefreshTokenUseCase,
                                               val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                               val logOutUseCase: LogOutUseCase,
                                               val uploadProfilePictureUseCase: UploadProfilePictureUseCase,
                                               val setAccountInfoUseCase: SetAccountInfoUseCase,
                                               val verifyEmailUseCase : SendEmailVerificationCodeUseCase,
                                               val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    private val _getAccountInfoResult = MutableLiveData<GetAccountInfoResult>()
    val getAccountInfoResult : LiveData<GetAccountInfoResult> = _getAccountInfoResult

    private val _refreshTokenResult = MutableLiveData<RefreshTokenResult>()
    val refreshTokenResult : LiveData<RefreshTokenResult> = _refreshTokenResult

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
            useCase(sharedPrefsModel.getIdToken(context)).onEach {
               _getAccountInfoResult.postValue(it)
            }.collect()
        }
    }

    fun refreshToken(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            refreshTokenUseCase(sharedPrefsModel.getRefreshToken(context)).onEach {
                if (it is SuccessRefreshTokenResult) {
                    saveAuthResponseUseCase(context,it.response.idToken,null,it.response.refreshToken,null)
                }
                _refreshTokenResult.postValue(it)
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

    fun resetGetAccountInfoViewModelState() {
        _refreshTokenResult.postValue(RefreshTokenResult())
        _uploadProfilePictureResult.postValue(UploadProfilePictureResult())
        _setAccountInfoResult.postValue(SetAccountInfoResult())
        _verifyEmailResult.postValue(VerifyEmailResult())
    }

}