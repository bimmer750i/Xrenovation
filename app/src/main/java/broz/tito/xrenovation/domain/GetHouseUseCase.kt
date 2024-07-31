package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.AddHouseResult
import broz.tito.xrenovation.data.add_house.entities.GetHouseResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHouseUseCase @Inject constructor(val repository: AddHouseRepository) {

    operator fun invoke(houseId : String) : Flow<GetHouseResult> {
        return repository.getHouse(houseId)
    }

}