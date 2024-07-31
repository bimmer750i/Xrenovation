package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.domain.GetHouseUseCase
import broz.tito.xrenovation.domain.GetPointsUseCase
import javax.inject.Inject

class MapFragmentViewModelFactory @Inject constructor(val getPointsUseCase: GetPointsUseCase,val getHouseUseCase: GetHouseUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapFragmentViewModel(getPointsUseCase,getHouseUseCase) as T
    }
}