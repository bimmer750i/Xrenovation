package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.AddHouseResult
import broz.tito.xrenovation.data.add_house.entities.House
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddHouseUseCase @Inject constructor(val repository: broz.tito.xrenovation.domain.AddHouseRepository) {

    operator fun invoke(localId: String,name : String, house: House,accessToken : String) : Flow<AddHouseResult> {
        return repository.addHouse(localId,name, house,accessToken)
    }

}