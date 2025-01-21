package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.add_house.entities.GetHouseResult
import broz.tito.xrenovation.data.get_houses.entities.GetPointResult
import broz.tito.xrenovation.domain.GetHouseUseCase
import broz.tito.xrenovation.domain.GetPointsUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapFragmentViewModel @Inject constructor(val getPointsUseCase: GetPointsUseCase,
                                                val getHouseUseCase: GetHouseUseCase) : ViewModel() {

    private val TAG = "MapFragmentViewModel"

    private val _getPointsResult = MutableLiveData<GetPointResult>()
    val getPointResult : LiveData<GetPointResult> = _getPointsResult

    private val _getHouseResult = MutableLiveData<GetHouseResult>()
    val getHouseResult : LiveData<GetHouseResult> = _getHouseResult

    fun getPoints() {
        viewModelScope.launch {
            getPointsUseCase().onEach {
                _getPointsResult.postValue(it)
            }.collect()
        }
    }

    fun getHouse(houseId : String) {
        viewModelScope.launch {
            getHouseUseCase(houseId).onEach {
                _getHouseResult.postValue(it)
            }.collect()
        }
    }

    fun resetMapFragmentViewModel() {
        _getHouseResult.postValue(GetHouseResult())
        _getPointsResult.postValue(GetPointResult())
    }

    fun resetHouseResult() {
        _getHouseResult.postValue(GetHouseResult())
    }



}