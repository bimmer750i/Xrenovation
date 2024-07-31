package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.domain.SearchPointUseCase
import javax.inject.Inject

class FindHouseOnMapViewModelFactory @Inject constructor(val searchPointUseCase: SearchPointUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FindHouseOnMapViewModel(searchPointUseCase) as T
    }
}