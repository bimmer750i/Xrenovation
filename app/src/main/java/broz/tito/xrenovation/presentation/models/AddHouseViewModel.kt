package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.add_house.entities.*
import broz.tito.xrenovation.data.auth.entities.GetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.*
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddHouseViewModel @Inject constructor(val getAccountInfoUseCase: GetAccountInfoUseCase,
                                            val refreshTokenUseCase: RefreshTokenUseCase,
                                            val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                            val sharedPrefsModel: SharedPrefsModel,
                                            val suggestAddressUseCase: SuggestAddressUseCase,
                                            val searchPointUseCase: SearchPointUseCase,
                                            val loadPhotosToFireBaseUseCase: LoadPhotosToFireBaseUseCase,
                                            val addHouseUseCase: AddHouseUseCase,
                                            val addHousePointUseCase: AddHousePointUseCase) : ViewModel() {

    private val _getAccountInfoResult = MutableLiveData<GetAccountInfoResult>()
    val getAccountInfoResult : LiveData<GetAccountInfoResult> = _getAccountInfoResult

    private val _refreshTokenResult = MutableLiveData<RefreshTokenResult>()
    val refreshTokenResult : LiveData<RefreshTokenResult> = _refreshTokenResult

    private val _suggestAddressResult = MutableLiveData<SuggestAddressResult>()
    val suggestAddressResult : LiveData<SuggestAddressResult> = _suggestAddressResult

    private val _searchPointResult = MutableLiveData<SearchPointResult>()
    val searchPointResult : LiveData<SearchPointResult> = _searchPointResult

    private val _loadPhotosResult = MutableLiveData<LoadPhotosResult>()
    val loadPhotosResult : LiveData<LoadPhotosResult> = _loadPhotosResult

    private val _addHouseResult = MutableLiveData<AddHouseResult>()
    val addHouseResult : LiveData<AddHouseResult> = _addHouseResult

    private val _addHousePointResult = MutableLiveData<AddHousePointResult>()
    val addHousePointResult : LiveData<AddHousePointResult> = _addHousePointResult

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

    fun suggestAddress(context: Context,address : String) {
        viewModelScope.launch(Dispatchers.IO) {
            suggestAddressUseCase(context, address).onEach {
                _suggestAddressResult.postValue(it)
            }.collect()
        }
    }

    fun searchPoint(point : Point) {
        viewModelScope.launch(Dispatchers.IO) {
            searchPointUseCase(point).onEach {
                _searchPointResult.postValue(it)
            }.collect()
        }
    }

    fun loadPhotosToFireBase(context: Context,path : String,list : ArrayList<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            loadPhotosToFireBaseUseCase(sharedPrefsModel.getLocalId(context),path,list).onEach {
                _loadPhotosResult.postValue(it)
            }.collect()
        }
    }

    fun addHouse(context: Context,name : String, house : House) {
        house.localid = sharedPrefsModel.getLocalId(context)
        viewModelScope.launch(Dispatchers.IO) {
            addHouseUseCase(sharedPrefsModel.getLocalId(context),"$name.json", house,sharedPrefsModel.getIdToken(context)).onEach {
                _addHouseResult.postValue(it)
            }.collect()
        }
    }

    fun addHousePoint(context: Context,houseId: String,housePoint: HousePoint) {
        viewModelScope.launch(Dispatchers.IO) {
            addHousePointUseCase(housePoint,houseId,sharedPrefsModel.getIdToken(context)).onEach {
                _addHousePointResult.postValue(it)
            }.collect()
        }
    }

    fun resetState() {
        _getAccountInfoResult.postValue(GetAccountInfoResult())
        _refreshTokenResult.postValue(RefreshTokenResult())
        _suggestAddressResult.postValue(SuggestAddressResult())
        _searchPointResult.postValue(SearchPointResult())
        _loadPhotosResult.postValue(LoadPhotosResult())
        _addHouseResult.postValue(AddHouseResult())
        _addHousePointResult.postValue(AddHousePointResult())
    }

}