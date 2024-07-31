package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.AddHouseCorrectionResult
import broz.tito.xrenovation.data.add_house.entities.HouseCorrection
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddHouseCorrectionUseCase @Inject constructor(val repository: AddHouseRepository) {

    operator fun invoke(localId : String, houseCorrection: HouseCorrection,accessToken : String) : Flow<AddHouseCorrectionResult> {
        return repository.addHouseCorrection(localId,houseCorrection,accessToken)
    }

}