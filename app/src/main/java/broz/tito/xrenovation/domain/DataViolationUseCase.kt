package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.ReportViolation
import broz.tito.xrenovation.data.add_house.entities.ReportViolationResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataViolationUseCase @Inject constructor(val repository: AddHouseRepository) {

    operator fun invoke(localId : String, body : ReportViolation, accessToken : String) : Flow<ReportViolationResult> {
        return repository.addDataViolation(localId, body, accessToken)
    }

}