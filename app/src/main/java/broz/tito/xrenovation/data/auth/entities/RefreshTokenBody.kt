package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class RefreshTokenBody (@SerializedName("refresh_token") val refreshToken : String) {
    @SerializedName("grant_type") val grantType = "refresh_token"
}