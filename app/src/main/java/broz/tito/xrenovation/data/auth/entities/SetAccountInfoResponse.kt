package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

data class SetAccountInfoResponse (

    @SerializedName("localId"          ) var localId          : String?,
    @SerializedName("email"            ) var email            : String?,
    @SerializedName("displayName"      ) var displayName      : String?,
    @SerializedName("photoUrl"         ) var photoUrl         : String?,
    @SerializedName("passwordHash"     ) var passwordHash     : String?,
    @SerializedName("providerUserInfo" ) var providerUserInfo : ArrayList<ProviderUserInfo>?,
    @SerializedName("idToken"          ) var idToken          : String?,
    @SerializedName("refreshToken"     ) var refreshToken     : String?,
    @SerializedName("expiresIn"        ) var expiresIn        : String?

)

data class ProviderUserInfo (

    @SerializedName("providerId"  ) var providerId  : String? = null,
    @SerializedName("federatedId" ) var federatedId : String? = null,
    @SerializedName("displayName" ) var displayName : String? = null,
    @SerializedName("photoUrl"    ) var photoUrl    : String? = null

)