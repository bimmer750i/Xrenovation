package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.DataDeletionRequest
import broz.tito.xrenovation.data.add_house.entities.DataDeletionRequestResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class DataDeletionRequestUseCase @Inject constructor(val repository : AddHouseRepository) {

    operator fun invoke(localId: String,
                        dataDeletionRequest: DataDeletionRequest,
                        accessToken: String) : Flow<DataDeletionRequestResult> = repository.addDataDeletionRequest(localId, dataDeletionRequest, accessToken)

}