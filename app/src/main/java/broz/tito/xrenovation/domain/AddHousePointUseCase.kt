package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.AddHousePointResult
import broz.tito.xrenovation.data.add_house.entities.HousePoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddHousePointUseCase @Inject constructor(val repository: AddHouseRepository) {

    operator fun invoke(housePoint: HousePoint,houseId: String,accessToken : String,localId : String) : Flow<AddHousePointResult> {
        return repository.addHousePoint(housePoint,houseId,accessToken,localId)
    }

}