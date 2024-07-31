package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.HousePoint
import broz.tito.xrenovation.data.get_houses.entities.GetPointResult
import broz.tito.xrenovation.data.get_houses.entities.RawSuccessGetPointResult
import broz.tito.xrenovation.data.get_houses.entities.SuccessGetPointResult
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPointsUseCase @Inject constructor(val repository: AddHouseRepository) {

    private val TAG = "GetPointsUseCase"

    operator fun invoke() : Flow<GetPointResult> {
        return repository.getPoints().map {
            if (it is RawSuccessGetPointResult) {
                val result = ArrayList<HousePoint>()
                it.rawHousePointArray.entrySet().forEach {
                    result.add(Gson().fromJson(it.value.toString(), HousePoint::class.java))
                }
                SuccessGetPointResult(result)
            }
            else {
                it
            }
        }
    }

}