package broz.tito.xrenovation.data.add_house

import android.content.Context
import broz.tito.xrenovation.data.add_house.entities.*
import broz.tito.xrenovation.data.get_houses.entities.GetPointResult
import broz.tito.xrenovation.domain.AddHouseRepository
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddHouseRepositoryImpl @Inject constructor(val model: HouseModel) : AddHouseRepository {

    override fun suggestAddress(context: Context, address: String): Flow<SuggestAddressResult> {
        return model.suggestAddress(context, address)
    }

    override fun searchPoint(point: Point): Flow<SearchPointResult> {
        return model.searchPoint(point)
    }

    override fun loadPhotosToFireBase(localId: String,path : String, list: ArrayList<String>): Flow<LoadPhotosResult> {
        return model.loadPhotosToFireBase(localId,path, list)
    }

    override fun addHouse(localId: String,name: String, house: House,accessToken : String): Flow<AddHouseResult> {
        return model.addHouse(localId,name,house,accessToken)
    }

    override fun addHousePoint(housePoint: HousePoint,houseId: String,accessToken : String): Flow<AddHousePointResult> {
        return model.addHousePoint(housePoint,houseId,accessToken)
    }

    override fun getPoints(): Flow<GetPointResult> {
        return model.getPoints()
    }

    override fun getHouse(houseId: String): Flow<GetHouseResult> {
        return model.getHouse(houseId)
    }

    override fun addComment(localId : String,houseId: String, comment: Comment,accessToken : String): Flow<AddCommentResult> {
        return model.addComment(localId,houseId, comment,accessToken)
    }

    override fun getComments(houseId: String): Flow<GetCommentsResult> {
        return model.getComments(houseId)
    }

    override fun addHouseCorrection(localId : String, houseCorrection: HouseCorrection,accessToken : String): Flow<AddHouseCorrectionResult> {
        return model.addHouseCorrection(localId,houseCorrection,accessToken)
    }



}