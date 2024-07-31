package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.SearchPointResult
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchPointUseCase @Inject constructor(val repository: broz.tito.xrenovation.domain.AddHouseRepository) {

    operator fun invoke(point: Point) : Flow<SearchPointResult> {
        return repository.searchPoint(point)
    }

}