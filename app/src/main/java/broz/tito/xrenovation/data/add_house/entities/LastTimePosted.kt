package broz.tito.xrenovation.data.add_house.entities

import com.google.gson.annotations.SerializedName

data class LastTimePosted(@SerializedName("localId") val localId : String, @SerializedName("lastTimePosted") var lastTimePosted : Long)