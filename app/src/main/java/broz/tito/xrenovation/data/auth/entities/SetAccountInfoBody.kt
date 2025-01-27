package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class SetAccountInfoBody(
    @SerializedName("idToken") val idToken : String?,
    @SerializedName("displayName") val displayName : String?,
    @SerializedName("photoUrl") val photoUrl : String?,
    @SerializedName("deleteAttribute") val deleteAttribute : ArrayList<String>?,
    @SerializedName("returnSecureToken") var returnSecureToken : Boolean?
) {

}