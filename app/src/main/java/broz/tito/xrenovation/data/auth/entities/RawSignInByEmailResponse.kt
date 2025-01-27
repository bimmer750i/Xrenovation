package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class RawSignInByEmailResponse (

    @SerializedName("localId"      ) var localId      : String?,
    @SerializedName("email"        ) var email        : String?,
    @SerializedName("displayName"  ) var displayName  : String?,
    @SerializedName("idToken"      ) var idToken      : String?,
    @SerializedName("registered"   ) var registered   : Boolean?,
    @SerializedName("refreshToken" ) var refreshToken : String?,
    @SerializedName("expiresIn"    ) var expiresIn    : String?

)