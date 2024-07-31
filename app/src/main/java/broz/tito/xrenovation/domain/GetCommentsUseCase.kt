package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.*
import broz.tito.xrenovation.presentation.entities.DisplayComment
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(val repository: AddHouseRepository) {

    operator fun invoke(houseId : String) : Flow<GetCommentsResult> {
        return repository.getComments(houseId).map {
            val result = ArrayList<DisplayComment>()
            if (it is RawSuccessGetCommentsResult) {
                it.jsonObject.entrySet().forEach {
                    result.add(DisplayComment(it.key,
                        Gson().fromJson(it.value.toString(), Comment::class.java)))
                }
            }
            SuccessGetCommentsResult(result)}
    }

}