package broz.tito.xrenovation.data.add_house.entities

import com.google.gson.annotations.SerializedName

data class HouseCorrection(@SerializedName("timeAdded") var timeAdded : Long, @SerializedName("houseId") val houseId : String, @SerializedName("localId") var localId : String, @SerializedName("correctionText") val  correctionText : String)