package broz.tito.xrenovation.data.get_houses.entities

import broz.tito.xrenovation.data.add_house.entities.House
import broz.tito.xrenovation.data.add_house.entities.HousePoint
import com.google.gson.JsonObject

open class GetPointResult

class PendingGetPointResult : GetPointResult()

class RawSuccessGetPointResult(val rawHousePointArray : JsonObject) : GetPointResult()

class SuccessGetPointResult(val points : ArrayList<HousePoint>) : GetPointResult()

class FailureGetPointResult(val errorMessage : String) : GetPointResult()