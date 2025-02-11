package broz.tito.xrenovation.presentation.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.R
import broz.tito.xrenovation.data.add_house.entities.*
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
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
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


    private val _suggestAddressResult = MutableLiveData<SuggestAddressResult>()
    val suggestAddressResult : LiveData<SuggestAddressResult> = _suggestAddressResult

    private val _addHouseResult = MutableLiveData<AddHouseResult>()
    val addHouseResult : LiveData<AddHouseResult> = _addHouseResult

    fun addHouse(context: Context,point: Point?,list : ArrayList<String>,name : String,house: House,housePoint: HousePoint) {
        viewModelScope.launch(Dispatchers.IO) {
            _addHouseResult.postValue(PendingAddHouseResult())
            // GETTING ACCOUNT INFO
            val getAccountInfoResult = getAccountInfo(context,getAccountInfoUseCase, sharedPrefsModel)
            when (getAccountInfoResult) {
                // ACCOUNT INFO RECEIVED SUCCESSFULLY
                is SuccessGetAccountInfoResult -> {
                    successGetAccountInfoFork(getAccountInfoResult, context, point, list, name, house, housePoint)
                }
                // FAILED TO RECEIVE ACCOUNT INFO
                is FailureGetAccountInfoResult -> {
                    when (getAccountInfoResult.errorMessage) {
                        // FAILED TO GET ACCOUNT BECAUSE OF INVALID TOKEN
                        INVALID_ID_TOKEN -> {
                            // REFRESHING ACCOUNT TOKEN AFTER FAILED TO GET ACCOUNT INFO
                            val refreshTokenResult = refreshToken(context, refreshTokenUseCase, sharedPrefsModel, saveAuthResponseUseCase)
                            when (refreshTokenResult) {
                                // TOKEN REFRESHED SUCCESSFULLY
                                is SuccessRefreshTokenResult -> {
                                    val getAccountInfoResultAgain = getAccountInfo(context, getAccountInfoUseCase, sharedPrefsModel)
                                    when(getAccountInfoResultAgain) {
                                        // ACCOUNT INFO SUCCEEDED AFTER REFRESHING TOKEN
                                        is SuccessGetAccountInfoResult -> {
                                            successGetAccountInfoFork(getAccountInfoResultAgain,context, point, list, name, house, housePoint)
                                        }
                                        // FAILED TO GET ACCOUNT INFO AFTER TOKEN REFRESH
                                        is FailureGetAccountInfoResult -> {
                                            _addHouseResult.postValue(FailureAddHouseResult(getAccountInfoResultAgain.errorMessage))
                                        }
                                    }
                                }
                                // FAILED TO REFRESH TOKEN
                                is FailureRefreshTokenResult -> {
                                    _addHouseResult.postValue(FailureAddHouseResult(refreshTokenResult.errorMessage))
                                }
                            }
                        }
                        // FAILED TO GET ACCOUNT INFO BECAUSE OF EVERYTHING ELSE
                        else -> {
                            _addHouseResult.postValue(FailureAddHouseResult(getAccountInfoResult.errorMessage))
                        }
                    }

                }
            }
        }
    }

    private suspend fun successGetAccountInfoFork(getAccountInfoResult: SuccessGetAccountInfoResult, context: Context,point: Point?,list : ArrayList<String>,name : String,house: House,housePoint: HousePoint) {
        getAccountInfoResult.user.emailVerified?.let { verified ->
            // EMAIL VERIFIED
            if (verified) {
                // SEARCHING FOR HOUSE POINT
                val searchPointResult = searchPoint(point)
                Log.d("AddHouseViewModel", searchPointResult::class.simpleName.toString())
                when (searchPointResult) {
                    // HOUSE POINT WAS FOUND SUCCESSFULLY
                    is SuccessSearchPointResult -> {
                        // HOUSE POINT BELONGS TO MOSCOW AND HAS THE HOUSE NUMBER
                        if (searchPointResult.searchPointAddress.isMoscow(context) && !searchPointResult.searchPointAddress.house.isNullOrEmpty()) {
                            // LOADING PHOTOS TO FIREBASE
                            val loadPhotosResult = loadPhotosToFireBase(context, UUID.randomUUID().toString().take(10),list)
                            when (loadPhotosResult) {
                                // PHOTOS LOADED SUCCESSFULLY
                                is SuccessLoadPhotosResult -> {
                                    // ADDING HOUSE IF PHOTOS LOADED SUCCESSFULLY
                                    // CHANGE HOUSE URL LIST
                                    house.photos = loadPhotosResult.urlList
                                    house.localid = sharedPrefsModel.getLocalId(context)
                                    val addHouseResult = addHouse(context, name, house)
                                    when (addHouseResult){
                                        // HOUSE ADDED SUCCESSFULLY
                                        is SuccessAddHouseResult -> {
                                            addHouseResult.houseResponse.name?.let {name ->
                                                if (point!= null) {
                                                    // HOUSE NAME AFTER RESPONSE RECEIVED IS NOT CHANGED, WE CAN CALL !!
                                                    val addHousePointResult = addHousePoint(context,addHouseResult.houseResponse.name!!,housePoint)
                                                    when (addHousePointResult) {
                                                        // ADDED HOUSE POINT SUCCESSFULLY
                                                        is SuccessAddHousePointResult -> {
                                                            _addHouseResult.postValue(SuccessAddHouseResult(addHouseResult.houseResponse))
                                                        }
                                                        // FAILED TO ADD HOUSE POINT
                                                        is FailureAddHousePointResult -> {
                                                            _addHouseResult.postValue(FailureAddHouseResult(addHousePointResult.errorMessage))
                                                        }
                                                    }
                                                }
                                                else {}
                                            }
                                        }
                                        // FAILED TO ADD HOUSE
                                        is FailureAddHouseResult -> {
                                            _addHouseResult.postValue(addHouseResult)
                                        }
                                        else -> {}
                                    }
                                }
                                // FAILED TO LOAD PHOTOS
                                is FailureLoadPhotosResult -> {
                                    _addHouseResult.postValue(FailureAddHouseResult(loadPhotosResult.errorMessage))
                                }
                                else -> {}
                            }
                        }
                        // HOUSE POINT DOESN'T BELONG TO MOSCOW
                        else if (!searchPointResult.searchPointAddress.isMoscow(context)) {
                            _addHouseResult.postValue(FailureAddHouseResult(ADDRESS_NOT_MOSCOW))
                        }
                        // HOUSE POINT DOESN'T HAVE HOUSE
                        else if (searchPointResult.searchPointAddress.house.isNullOrEmpty()) {
                            _addHouseResult.postValue(FailureAddHouseResult(ADDRESS_NO_HOUSE))
                        }
                        else {
                            Log.d("AddHouseViewModel", "ELSE BRANCH ON HOUSE POINT")
                        }
                    }
                    // FAILED TO FIND HOUSE POINT
                    is FailureSearchPointResult -> {
                        _addHouseResult.postValue(FailureAddHouseResult(searchPointResult.errorMessage))
                    }
                    else -> {}

                }
            }
            else {
                // EMAIL NOT VERIFIED
                _addHouseResult.postValue(FailureAddHouseResult(EMAIL_NOT_VERIFIED))
            }
        }
    }

    fun suggestAddress(context: Context,address : String) {
        viewModelScope.launch(Dispatchers.IO) {
            suggestAddressUseCase(context, address).onEach {
                _suggestAddressResult.postValue(it)
            }.collect()
        }
    }

    private suspend fun addHousePoint(context: Context,houseId: String,housePoint: HousePoint) : AddHousePointResult = coroutineScope {
        async {
            addHousePointUseCase(housePoint,houseId,sharedPrefsModel.getIdToken(context),sharedPrefsModel.getLocalId(context)).last()
        }.await()
    }

    private suspend fun searchPoint(point : Point?) : SearchPointResult = coroutineScope {
        async {
            if (point != null) {
                searchPointUseCase(point).last()
            }
            else {
                return@async FailureSearchPointResult(NULL_POINT)
            }
        }.await()
    }

    private suspend fun loadPhotosToFireBase(context: Context,path : String,list : ArrayList<String>) : LoadPhotosResult = coroutineScope {
        async {
            loadPhotosToFireBaseUseCase(sharedPrefsModel.getLocalId(context),path,list,sharedPrefsModel.getIdToken(context)).last()
        }.await()
    }

    private suspend fun addHouse(context: Context,name : String, house : House) : AddHouseResult = coroutineScope {
        async(Dispatchers.IO) {
            house.localid = sharedPrefsModel.getLocalId(context)
            addHouseUseCase(sharedPrefsModel.getLocalId(context),"$name.json", house,sharedPrefsModel.getIdToken(context)).last()
        }.await()
    }

    fun resetState() {
        _suggestAddressResult.postValue(SuggestAddressResult())
        _addHouseResult.postValue(AddHouseResult())
    }

}