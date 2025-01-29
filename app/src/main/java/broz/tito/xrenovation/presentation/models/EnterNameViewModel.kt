package broz.tito.xrenovation.presentation.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.FailureRefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.FailureSetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.PendingGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.PendingSetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessSetAccountInfoResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import broz.tito.xrenovation.domain.SetAccountInfoUseCase
import broz.tito.xrenovation.presentation.INVALID_ID_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
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

    fun setAccountInfo(context : Context,
                       displayName: String?,
                       photoUrl: String?,
                       deleteAttribute: ArrayList<String>?) {
        viewModelScope.launch {
            _setAccountInfoResult.postValue(PendingSetAccountInfoResult())
            val setAccountInfoResult = setAccountInfo1(context, displayName, photoUrl, deleteAttribute)
            when (setAccountInfoResult) {
                // SUCCESS SETTING ACCOUNT INFO
                is SuccessSetAccountInfoResult -> {
                    _setAccountInfoResult.postValue(setAccountInfoResult)
                }
                // FAILED TO SET ACCOUNT INFO
                is FailureSetAccountInfoResult -> {
                    when (setAccountInfoResult.errorMessage) {
                        // FAILED TO SET ACCOUNT INFO BECAUSE OF INVALID TOKEN
                        INVALID_ID_TOKEN -> {
                            val refreshTokenResult = refreshToken(context, refreshTokenUseCase, sharedPrefsModel, saveAuthResponseUseCase)
                            when (refreshTokenResult) {
                                // TOKEN REFRESH WAS SUCCESSFUL
                                is SuccessRefreshTokenResult -> {
                                    // SETTING ACCOUNT INFO AGAIN
                                    val setAccountInfoResultAgain = setAccountInfo1(context, displayName, photoUrl, deleteAttribute)
                                    when (setAccountInfoResultAgain) {
                                        is SuccessSetAccountInfoResult -> {
                                            _setAccountInfoResult.postValue(setAccountInfoResultAgain)
                                        }
                                        is FailureSetAccountInfoResult -> {
                                            _setAccountInfoResult.postValue(setAccountInfoResultAgain)
                                        }
                                    }
                                }
                                is FailureRefreshTokenResult -> {
                                    _setAccountInfoResult.postValue(FailureSetAccountInfoResult(refreshTokenResult.errorMessage))
                                }
                            }
                        }
                        // FAILED TO SET ACCOUNT INFO BECAUSE OF EVERYTHING ELSE
                        else -> {
                            _setAccountInfoResult.postValue(setAccountInfoResult)
                        }
                    }
                }
            }
        }
    }

    private suspend fun setAccountInfo1(context : Context,
                                       displayName: String?,
                                       photoUrl: String?,
                                       deleteAttribute: ArrayList<String>?) : SetAccountInfoResult = coroutineScope {
        async {

            var name = displayName
            if (displayName.isNullOrEmpty()) {
                name = "user-${sharedPrefsModel.getLocalId(context).take(10)}"
            }
            return@async setAccountInfoUseCase(sharedPrefsModel.getIdToken(context),name, photoUrl, deleteAttribute).onEach {
                if (it is SuccessSetAccountInfoResult) {
                    saveAuthResponseUseCase(context,it.response.idToken,it.response.email,it.response.refreshToken,it.response.localId)
                }
                _setAccountInfoResult.postValue(it)
            }.last()
        }.await()
    }


    fun resetState() {
        _setAccountInfoResult.postValue(SetAccountInfoResult())
    }


}