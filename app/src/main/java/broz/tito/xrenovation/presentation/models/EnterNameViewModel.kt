package broz.tito.xrenovation.presentation.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessSetAccountInfoResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import broz.tito.xrenovation.domain.SetAccountInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class EnterNameViewModel @Inject constructor(val setAccountInfoUseCase: SetAccountInfoUseCase,
                                             val refreshTokenUseCase: RefreshTokenUseCase,
                                             val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                             val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    private val TAG = "EnterNameViewModel"

    private val _setAccountInfoResult = MutableLiveData<SetAccountInfoResult>()
    val setAccountInfoResult : LiveData<SetAccountInfoResult> = _setAccountInfoResult

    private val _refreshTokenResult = MutableLiveData<RefreshTokenResult>()
    val refreshTokenResult : LiveData<RefreshTokenResult> = _refreshTokenResult

    fun setAccountInfo(context : Context,
                       displayName: String?,
                       photoUrl: String?,
                       deleteAttribute: ArrayList<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            var name = displayName
            if (displayName.isNullOrEmpty()) {
                name = "user-${sharedPrefsModel.getLocalId(context).take(10)}"
            }
            setAccountInfoUseCase(sharedPrefsModel.getIdToken(context),name, photoUrl, deleteAttribute).onEach {
                if (it is SuccessSetAccountInfoResult) {
                    saveAuthResponseUseCase(context,it.response.idToken,it.response.email,it.response.refreshToken,it.response.localId)
                }
                _setAccountInfoResult.postValue(it)
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

    fun resetState() {
        _refreshTokenResult.postValue(RefreshTokenResult())
        _setAccountInfoResult.postValue(SetAccountInfoResult())
    }


}