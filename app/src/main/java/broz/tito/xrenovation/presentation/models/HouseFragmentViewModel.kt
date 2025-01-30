package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.add_house.entities.AddCommentResult
import broz.tito.xrenovation.data.add_house.entities.AddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.Comment
import broz.tito.xrenovation.data.add_house.entities.FailureAddCommentResult
import broz.tito.xrenovation.data.add_house.entities.GetCommentsResult
import broz.tito.xrenovation.data.add_house.entities.PendingAddCommentResult
import broz.tito.xrenovation.data.add_house.entities.SuccessAddCommentResult
import broz.tito.xrenovation.data.auth.entities.FailureGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.FailureRefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.GetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import broz.tito.xrenovation.presentation.EMAIL_NOT_VERIFIED
import broz.tito.xrenovation.presentation.INVALID_ID_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
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

    /*fun getAccountInfo(context: Context) {
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
    }*/

    private suspend fun addComment1(context : Context,houseId : String, text : String, displayName : String?, photoUrl : String?) : AddCommentResult  = coroutineScope {
        async(Dispatchers.IO) {
            addCommentUseCase(sharedPrefsModel.getLocalId(context),houseId, Comment(houseId,0L,displayName ?: NO_DISPLAY_NAME,
                sharedPrefsModel.getLocalId(context),photoUrl ?: "",0,false,"",text),
                sharedPrefsModel.getIdToken(context)).last()
        }.await()
    }


    fun addComment(context : Context,houseId : String, text : String) {
            viewModelScope.launch(Dispatchers.IO) {
                _addCommentResult.postValue(PendingAddCommentResult())
                // GETTING ACCOUNT INFO
                val getAccountInfoResult = getAccountInfo(context, getAccountInfoUseCase, sharedPrefsModel)
                when (getAccountInfoResult) {
                    // GETTING ACCOUNT INFO WAS SUCCESSFUL
                    is SuccessGetAccountInfoResult -> {
                        getAccountInfoResult.user.emailVerified?.let { verified ->
                            // ADDING COMMENT IF EMAIL IS VERIFIED
                            if (verified && getAccountInfoResult.user.localId != null) {
                                val addCommentResult = addComment1(context, houseId, text, getAccountInfoResult.user.displayName, getAccountInfoResult.user.photoUrl)
                                when (addCommentResult) {
                                    // COMMENT ADDED SUCCESSFULLY
                                    is SuccessAddCommentResult -> {
                                        _addCommentResult.postValue(addCommentResult)
                                    }
                                    // FAILED TO ADD COMMENT
                                    is FailureAddCommentResult -> {
                                        _addCommentResult.postValue((addCommentResult))
                                    }
                                }
                            }
                            // IF EMAIL NOT VERIFIED
                            else {
                                _addCommentResult.postValue(FailureAddCommentResult(EMAIL_NOT_VERIFIED))
                            }
                        }
                    }
                    // FAILED TO GET ACCOUNT INFO
                    is FailureGetAccountInfoResult -> {
                        when (getAccountInfoResult.errorMessage) {
                            // FAILED TO GET ACCOUNT INFO BECAUSE OF INVALID TOKEN
                            INVALID_ID_TOKEN -> {
                                // REFRESHING TOKEN
                                val refreshTokenResult = refreshToken(context, refreshTokenUseCase, sharedPrefsModel, saveAuthResponseUseCase)
                                when (refreshTokenResult) {
                                    // TOKEN REFRESHED SUCCESSFULLY
                                    is SuccessRefreshTokenResult -> {
                                        // GETTING ACCOUNT INFO AFTER TOKEN REFRESH WAS SUCCESSFUL
                                        val getAccountInfoResultAgain = getAccountInfo(context, getAccountInfoUseCase, sharedPrefsModel)
                                        when (getAccountInfoResultAgain) {
                                            // GETTING ACCOUNT INFO AGAIN WAS SUCCESSFUL
                                            is SuccessGetAccountInfoResult -> {
                                                val addCommentResultAgain = addComment1(context, houseId, text, getAccountInfoResultAgain.user.displayName, getAccountInfoResultAgain.user.photoUrl)
                                                _addCommentResult.postValue(addCommentResultAgain)
                                            }
                                            // FAILED TO GET ACCOUNT AGAIN INFO AFTER REFRESH
                                            is FailureGetAccountInfoResult -> {
                                                _addCommentResult.postValue(FailureAddCommentResult(getAccountInfoResultAgain.errorMessage))
                                            }
                                        }
                                    }
                                    // FAILED TO REFRESH TOKEN
                                    is FailureRefreshTokenResult -> {
                                        _addCommentResult.postValue(FailureAddCommentResult(refreshTokenResult.errorMessage))
                                    }
                                }
                            }
                            // FAILED TO GET ACCOUNT INFO FOR ANOTHER REASON
                            else -> {
                                _addCommentResult.postValue(FailureAddCommentResult(getAccountInfoResult.errorMessage))
                            }
                        }
                    }
                }
            }
    }

    fun getComments(houseId: String) {
        viewModelScope.launch {
            getCommentsUseCase(houseId).onEach {
                _getCommentsResult.postValue(it)
            }.collect()
        }
    }

    fun resetCommentState() {
        if (_addCommentResult.value is FailureAddCommentResult || _addCommentResult.value is SuccessAddCommentResult ) {
            _addCommentResult.postValue(AddCommentResult())
        }
    }

    fun resetState() {
        _getAccountInfoResult.postValue(GetAccountInfoResult())
        _refreshTokenResult.postValue(RefreshTokenResult())
        _addCommentResult.postValue(AddCommentResult())
    }

}