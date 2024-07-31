package broz.tito.xrenovation.data.add_house.entities

import com.google.gson.annotations.SerializedName

class House(
    @SerializedName("point")
    val point : LatLon,
    @SerializedName("address")
    val address : String,
    @SerializedName("floors")
    val floors : String,
    @SerializedName("flats")
    val flats : String,
    @SerializedName("year")
    val year : String,
    @SerializedName("description")
    val description : String,
    @SerializedName("photos")
    val photos : ArrayList<String>,
    @SerializedName("links")
    val links : ArrayList<String>
) : java.io.Serializable {
    constructor() : this(LatLon(0.0,0.0),"","","","","", arrayListOf(), arrayListOf())
}