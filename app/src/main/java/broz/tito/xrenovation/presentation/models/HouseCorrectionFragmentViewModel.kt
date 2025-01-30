package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.*
import broz.tito.xrenovation.data.add_house.entities.AddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.FailureAddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.HouseCorrection
import broz.tito.xrenovation.data.add_house.entities.PendingAddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.SuccessAddHouseCorrectionResult
import broz.tito.xrenovation.data.auth.entities.FailureGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.FailureRefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.GetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.PendingGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.AddHouseCorrectionUseCase
import broz.tito.xrenovation.domain.GetAccountInfoUseCase
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
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

class HouseCorrectionFragmentViewModel @Inject constructor(val getAccountInfoUseCase: GetAccountInfoUseCase,
                                                           val refreshTokenUseCase: RefreshTokenUseCase,
                                                           val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                           val usecase : AddHouseCorrectionUseCase,
                                                           val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    private val _addHouseCorrectionResult = MutableLiveData<AddHouseCorrectionResult>()
    val addHouseCorrectionResult: LiveData<AddHouseCorrectionResult> = _addHouseCorrectionResult


    private suspend fun refreshToken(context: Context) : RefreshTokenResult = coroutineScope { async(Dispatchers.IO) {
        return@async refreshTokenUseCase(sharedPrefsModel.getRefreshToken(context)).onEach {
            if (it is SuccessRefreshTokenResult) {
                saveAuthResponseUseCase(
                    context,
                    it.response.idToken,
                    null,
                    it.response.refreshToken,
                    null
                )
            }
        }.last()
    }.await()
    }

    private suspend fun addHouseCorrection(context: Context, houseCorrection: HouseCorrection) : AddHouseCorrectionResult = coroutineScope {
        async {
            return@async usecase(
                sharedPrefsModel.getLocalId(context),
                houseCorrection.apply { this.localId = sharedPrefsModel.getLocalId(context) },
                sharedPrefsModel.getIdToken(context)
            ).last()
        }.await()
    }

    fun addHouseCorrection3(context: Context, houseCorrection: HouseCorrection) {
        viewModelScope.launch {
            _addHouseCorrectionResult.postValue(PendingAddHouseCorrectionResult())
            val accountInfoResult = getAccountInfo(context,getAccountInfoUseCase, sharedPrefsModel)

            when(accountInfoResult) {
                is SuccessGetAccountInfoResult -> {
                    accountInfoResult.user.emailVerified?.let { verified ->
                        if (verified && accountInfoResult.user.localId != null) {
                            val addHouseCorrectionResult = addHouseCorrection(context,houseCorrection)
                            if (addHouseCorrectionResult is SuccessAddHouseCorrectionResult) {
                                _addHouseCorrectionResult.postValue(addHouseCorrectionResult)
                            }
                            else {
                                _addHouseCorrectionResult.postValue(addHouseCorrectionResult)
                            }
                        }
                        else {
                            _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(EMAIL_NOT_VERIFIED))
                        }
                    }
                }

                is FailureGetAccountInfoResult -> {
                    // UNABLE TO GET ACCOUNT INFO FORK
                    when (accountInfoResult.errorMessage) {
                        INVALID_ID_TOKEN -> {
                            // IF GETTING ACCOUNT INFO FAILED BECAUSE OF INVALID ID TOKEN, REFRESHING TOKEN IS STARTED
                            val refreshTokenResult = refreshToken(context)
                            when (refreshTokenResult) {
                                // IF TOKEN WAS REFRESHED SUCCESSFULLY, GETTING ACCOUNT INFO
                                is SuccessRefreshTokenResult -> {
                                    val getAccountInfoResultAgain = getAccountInfo(context,getAccountInfoUseCase, sharedPrefsModel)
                                    when (getAccountInfoResultAgain) {
                                        // IF GETTING ACCOUNT INFO WAS SUCCESSFUL AFTER REFRESH, CHECKING IF EMAIL VERIFIED IS STARTED
                                        is SuccessGetAccountInfoResult -> {
                                            getAccountInfoResultAgain.user.emailVerified?.let { verified ->
                                                // EMAIL IS VERIFIED
                                                if (verified && getAccountInfoResultAgain.user.localId != null) {
                                                    val addHouseCorrectionResultAgain = addHouseCorrection(context,houseCorrection)
                                                    // SUCCESSFUL CORRECTION ADDITION
                                                    if (addHouseCorrectionResultAgain is SuccessAddHouseCorrectionResult) {
                                                        _addHouseCorrectionResult.postValue(addHouseCorrectionResultAgain)
                                                    }
                                                    // ADDITION WAS NOT SUCCESSFUL
                                                    else {
                                                        _addHouseCorrectionResult.postValue(addHouseCorrectionResultAgain)
                                                    }
                                                }
                                                // EMAIL WAS NOT VERIFIED
                                                else {
                                                    _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(EMAIL_NOT_VERIFIED))
                                                }
                                            }
                                        }
                                        // IF GETTING ACCOUNT INFO AFTER TOKEN REFRESH WAS UNSUCCESSFUL, THE ERROR IS DISPLAYED
                                        is FailureGetAccountInfoResult -> {
                                            _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(getAccountInfoResultAgain.errorMessage))
                                        }
                                    }
                                }
                                //IF TOKEN REFRESH FAILED, THE ERROR IS DISPLAYED
                                is FailureRefreshTokenResult -> {
                                    _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(refreshTokenResult.errorMessage))
                                }
                            }
                        }
                        // GETTING ACCOUNT INFO FAILED FOR ANOTHER REASON
                        else -> {
                            _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(accountInfoResult.errorMessage))
                        }
                    }
                }
            }
        }
    }

    fun resetModel() {
        _addHouseCorrectionResult.postValue(AddHouseCorrectionResult())
    }

    /*fun addHouseCorrection2(context: Context, houseCorrection: HouseCorrection) {
        viewModelScope.launch(Dispatchers.IO) {
            getAccountInfoUseCase(sharedPrefsModel.getIdToken(context)).onEach { accountInfoResult ->
                when (accountInfoResult) {
                    is PendingGetAccountInfoResult -> {
                        _addHouseCorrectionResult.postValue(PendingAddHouseCorrectionResult())
                    }
                    is SuccessGetAccountInfoResult -> {
                        accountInfoResult.user.emailVerified?.let {verified ->
                            if (verified && accountInfoResult.user.localId != null) {
                                usecase(
                                    sharedPrefsModel.getLocalId(context),
                                    houseCorrection,
                                    sharedPrefsModel.getIdToken(context)
                                ).onEach { houseCorrectionResult ->
                                    when(houseCorrectionResult) {
                                        is SuccessAddHouseCorrectionResult -> {
                                            _addHouseCorrectionResult.postValue(houseCorrectionResult)
                                        }
                                        is FailureAddHouseCorrectionResult -> {
                                            _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(houseCorrectionResult.errorMessage))
                                        }
                                    }
                                }.collect()
                            }
                            else {
                                _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(
                                    EMAIL_NOT_VERIFIED))
                            }
                    }
                        }
                    is FailureGetAccountInfoResult -> {
                        when (accountInfoResult.errorMessage) {
                            INVALID_ID_TOKEN -> {
                                refreshTokenUseCase(sharedPrefsModel.getRefreshToken(context))
                                    .onEach { refreshTokenResult ->
                                        when (refreshTokenResult) {
                                            is SuccessRefreshTokenResult -> {
                                                saveAuthResponseUseCase(
                                                    context,
                                                    refreshTokenResult.response.idToken,
                                                    null,
                                                    refreshTokenResult.response.refreshToken,
                                                    null
                                                )
                                                getAccountInfoUseCase(sharedPrefsModel.getIdToken(context)).onEach { accountInfoResultAgain ->
                                                    when (accountInfoResultAgain) {
                                                        is SuccessGetAccountInfoResult -> {
                                                            accountInfoResultAgain.user.emailVerified?.let {verified ->
                                                                if (verified && accountInfoResultAgain.user.localId != null) {
                                                                    usecase(
                                                                        sharedPrefsModel.getLocalId(context),
                                                                        houseCorrection,
                                                                        sharedPrefsModel.getIdToken(context)
                                                                    ).onEach { houseCorrectionResultAgain ->
                                                                        when(houseCorrectionResultAgain) {
                                                                            is SuccessAddHouseCorrectionResult -> {
                                                                                _addHouseCorrectionResult.postValue(houseCorrectionResultAgain)
                                                                            }
                                                                            is FailureAddHouseCorrectionResult -> {
                                                                                _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(houseCorrectionResultAgain.errorMessage))
                                                                            }
                                                                        }
                                                                    }.collect()
                                                                }
                                                                else {
                                                                    _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(
                                                                        EMAIL_NOT_VERIFIED))
                                                                }
                                                            }
                                                        }
                                                        is FailureGetAccountInfoResult -> {
                                                            _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(accountInfoResultAgain.errorMessage))
                                                        }
                                                    }
                                                }.collect()
                                            }
                                            is FailureRefreshTokenResult -> {
                                                _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(refreshTokenResult.errorMessage))
                                            }
                                        }
                                    }.collect()
                            }
                            else -> {
                                _addHouseCorrectionResult.postValue(FailureAddHouseCorrectionResult(accountInfoResult.errorMessage))
                            }
                        }
                    }
                }
            }.collect()
        }
    }*/
}

