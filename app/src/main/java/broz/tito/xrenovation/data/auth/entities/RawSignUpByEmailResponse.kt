package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName


data class RawSignUpByEmailResponse (
    @SerializedName("idToken"      ) val idToken      : String?,
    @SerializedName("email"        ) val email        : String?,
    @SerializedName("refreshToken" ) val refreshToken : String?,
    @SerializedName("expiresIn"    ) val expiresIn    : String?,
    @SerializedName("localId"      ) val localId      : String?)