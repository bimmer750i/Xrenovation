package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.*
import broz.tito.xrenovation.data.add_house.entities.AddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.HouseCorrection
import broz.tito.xrenovation.data.auth.entities.GetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.AddHouseCorrectionUseCase
import broz.tito.xrenovation.domain.GetAccountInfoUseCase
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class HouseCorrectionFragmentViewModel @Inject constructor(val getAccountInfoUseCase: GetAccountInfoUseCase,
                                                           val refreshTokenUseCase: RefreshTokenUseCase,
                                                           val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                           val usecase : AddHouseCorrectionUseCase,
                                                           val sharedPrefsModel: SharedPrefsModel) : ViewModel(){

    private val _getAccountInfoResult = MutableLiveData<GetAccountInfoResult>()
    val getAccountInfoResult : LiveData<GetAccountInfoResult> = _getAccountInfoResult

    private val _refreshTokenResult = MutableLiveData<RefreshTokenResult>()
    val refreshTokenResult : LiveData<RefreshTokenResult> = _refreshTokenResult

    private val _addHouseCorrectionResult = MutableLiveData<AddHouseCorrectionResult>()
    val addHouseCorrectionResult : LiveData<AddHouseCorrectionResult> = _addHouseCorrectionResult

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

    fun addHouseCorrection(context: Context, houseCorrection: HouseCorrection) {
        viewModelScope.launch {
            usecase(sharedPrefsModel.getLocalId(context),houseCorrection,sharedPrefsModel.getIdToken(context)).onEach {
                _addHouseCorrectionResult.postValue(it)
            }.collect()
        }
    }

    fun resetModel() {
        _getAccountInfoResult.postValue(GetAccountInfoResult())
        _refreshTokenResult.postValue(RefreshTokenResult())
        _addHouseCorrectionResult.postValue(AddHouseCorrectionResult())
    }

}