package broz.tito.xrenovation.data.add_house.entities

import android.content.Context
import broz.tito.xrenovation.R
import com.google.gson.annotations.SerializedName

open class SearchPointResult

class PendingSearchPointResult : SearchPointResult()

class SuccessSearchPointResult(val searchPointAddress: SearchPointAddress) : SearchPointResult()

class FailureSearchPointResult(val errorMessage : String) : SearchPointResult()

class SearchPointAddress(val province : String?, val area : String?, val locality : String?, val street : String?, val house : String? ) : java.io.Serializable {

    override fun toString(): String {
        return listOfNotNull(area,locality,street,house).joinToString("\n")
    }

    fun toAddress() : String {
        return listOfNotNull(area,locality,street,house).joinToString(", ")
    }

    fun toShortAddress() : String {
        return listOfNotNull(street,house,locality).joinToString(", ")
    }

}

fun SearchPointAddress.isMoscow(context: Context) : Boolean {
    return (this.province == context.getString(R.string.cfo)) && (this.area == context.getString(R.string.nao) || this.area == context.getString(R.string.tao) || this.locality == context.getString(R.string.zelenograd) || this.locality == context.getString(R.string.pos_akulovo) ||
            this.locality == context.getString(R.string.der_tolstop) || this.locality == context.getString(R.string.pos_tolstop) ||
            this.locality == context.getString(R.string.pos_rublevo) || this.locality == context.getString(R.string.pos_vnukovo) ||
            this.locality == context.getString(R.string.moscow))
}

class LatLon(
    @SerializedName("latitude")
    val latitude : Double,
    @SerializedName("longitude")
    val longitude : Double) : java.io.Serializable