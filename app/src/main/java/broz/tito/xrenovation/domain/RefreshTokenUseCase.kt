package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(val emailRepository: broz.tito.xrenovation.domain.EmailRepository) {

    operator fun invoke(refreshToken : String) : Flow<RefreshTokenResult> {
        return emailRepository.refreshToken(refreshToken)
    }

}