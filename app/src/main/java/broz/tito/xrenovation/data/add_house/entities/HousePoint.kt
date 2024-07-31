package broz.tito.xrenovation.data.add_house.entities

import com.google.gson.annotations.SerializedName

class HousePoint( @SerializedName("houseId") val houseId : String, @SerializedName("point") val latLon: LatLon)