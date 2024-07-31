package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class SetAccountInfoBody(
    @SerializedName("idToken") val idToken : String,
    @SerializedName("displayName") val displayName : String? = null,
    @SerializedName("photoUrl") val photoUrl : String? = null,
    @SerializedName("deleteAttribute") val deleteAttribute : ArrayList<String>? = null,
    @SerializedName("returnSecureToken") var returnSecureToken : Boolean
) {

}