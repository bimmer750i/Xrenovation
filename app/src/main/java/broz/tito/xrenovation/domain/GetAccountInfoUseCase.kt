package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.GetAccountInfoResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountInfoUseCase @Inject constructor(val repository: broz.tito.xrenovation.domain.EmailRepository) {

    operator fun invoke(idToken : String) : Flow<GetAccountInfoResult> {
        return repository.getAccountInfo(idToken)
    }

}