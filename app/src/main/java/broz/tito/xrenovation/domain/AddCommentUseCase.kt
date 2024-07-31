package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.AddCommentResult
import broz.tito.xrenovation.data.add_house.entities.Comment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(val repository : AddHouseRepository) {

    operator fun invoke(localId : String,houseId : String,comment : Comment,accessToken : String) : Flow<AddCommentResult> {
        return repository.addComment(localId,houseId, comment,accessToken)
    }

}