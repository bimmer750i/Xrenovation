package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName


data class RawSignUpByEmailResponse (
    @SerializedName("idToken"      ) var idToken      : String = "",
    @SerializedName("email"        ) var email        : String = "",
    @SerializedName("refreshToken" ) var refreshToken : String = "",
    @SerializedName("expiresIn"    ) var expiresIn    : String = "",
    @SerializedName("localId"      ) var localId      : String = "")