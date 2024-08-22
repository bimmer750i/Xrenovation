package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.add_house.entities.AddCommentResult
import broz.tito.xrenovation.data.add_house.entities.AddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.Comment
import broz.tito.xrenovation.data.add_house.entities.GetCommentsResult
import broz.tito.xrenovation.data.auth.entities.GetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class HouseFragmentViewModel @Inject constructor(val addCommentUseCase: AddCommentUseCase,
                                                 val getCommentsUseCase: GetCommentsUseCase,
                                                 val getAccountInfoUseCase: GetAccountInfoUseCase,
                                                 val refreshTokenUseCase: RefreshTokenUseCase,
                                                 val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                 val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    private val _getAccountInfoResult = MutableLiveData<GetAccountInfoResult>()
    val getAccountInfoResult : LiveData<GetAccountInfoResult> = _getAccountInfoResult

    private val _refreshTokenResult = MutableLiveData<RefreshTokenResult>()
    val refreshTokenResult : LiveData<RefreshTokenResult> = _refreshTokenResult

    private val _addCommentResult  = MutableLiveData<AddCommentResult>()
    val addCommentResult : LiveData<AddCommentResult> = _addCommentResult

    private val _getCommentsResult = MutableLiveData<GetCommentsResult>()
    val getCommentsResult : LiveData<GetCommentsResult> = _getCommentsResult

    fun getAccountInfo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccountInfoUseCase(sharedPrefsModel.getIdToken(context)).onEach {
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

    fun addComment(context : Context,houseId : String, text : String, displayName : String?, localId : String?, photoUrl : String?) {
        viewModelScope.launch {
            addCommentUseCase(sharedPrefsModel.getLocalId(context),houseId, Comment(houseId,0L,displayName!!,localId!!,photoUrl ?: "",0,false,"",text),sharedPrefsModel.getIdToken(context)).onEach {
                _addCommentResult.postValue(it)
            }.collect()
        }
    }

    fun getComments(houseId: String) {
        viewModelScope.launch {
            getCommentsUseCase(houseId).onEach {
                _getCommentsResult.postValue(it)
            }.collect()
        }
    }

    fun resetState() {
        _getAccountInfoResult.postValue(GetAccountInfoResult())
        _refreshTokenResult.postValue(RefreshTokenResult())
        _addCommentResult.postValue(AddCommentResult())
    }

}