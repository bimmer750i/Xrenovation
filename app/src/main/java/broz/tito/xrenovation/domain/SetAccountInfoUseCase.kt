package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.auth.entities.SetAccountInfoResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetAccountInfoUseCase @Inject constructor(val emailRepository: broz.tito.xrenovation.domain.EmailRepository) {

    operator fun invoke(idToken: String,
                        displayName: String?,
                        photoUrl: String?,
                        deleteAttribute: ArrayList<String>?) : Flow<SetAccountInfoResult> {
        return emailRepository.setAccountInfo(idToken, displayName, photoUrl, deleteAttribute)
    }

}