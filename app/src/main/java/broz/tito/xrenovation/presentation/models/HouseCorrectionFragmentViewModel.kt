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
            usecase(sharedPrefsModel.getLocalId(context),houseCorrection,"").onEach {
                _addHouseCorrectionResult.postValue(it)
            }.collect()
        }
    }

    fun resetModel() {
        _addHouseCorrectionResult.postValue(AddHouseCorrectionResult())
    }

}

