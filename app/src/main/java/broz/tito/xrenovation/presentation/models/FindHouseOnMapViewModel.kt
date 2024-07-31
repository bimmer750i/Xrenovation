package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.add_house.entities.SearchPointResult
import broz.tito.xrenovation.domain.SearchPointUseCase
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class FindHouseOnMapViewModel @Inject constructor(val searchPointUseCase: SearchPointUseCase) : ViewModel() {

    private val _searchPointResult = MutableLiveData<SearchPointResult>()
    val searchPointResult : LiveData<SearchPointResult> = _searchPointResult

    fun searchPoint(point : Point) {
        viewModelScope.launch(Dispatchers.IO) {
            searchPointUseCase(point).onEach {
                _searchPointResult.postValue(it)
            }.collect()
        }
    }

}