package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class GetAccountInfoResponse (

    @SerializedName("users" ) var users : ArrayList<User> = arrayListOf()

)

data class User (

    @SerializedName("localId"           ) var localId           : String?                     = null,
    @SerializedName("email"             ) var email             : String?                     = null,
    @SerializedName("emailVerified"     ) var emailVerified     : Boolean?                    = null,
    @SerializedName("displayName"       ) var displayName       : String?                     = null,
    @SerializedName("providerUserInfo"  ) var providerUserInfo  : ArrayList<ProviderInfo> = arrayListOf(),
    @SerializedName("photoUrl"          ) var photoUrl          : String?                     = null,
    @SerializedName("passwordHash"      ) var passwordHash      : String?                     = null,
    @SerializedName("passwordUpdatedAt" ) var passwordUpdatedAt : String?                        = null,
    @SerializedName("validSince"        ) var validSince        : String?                     = null,
    @SerializedName("disabled"          ) var disabled          : Boolean?                    = null,
    @SerializedName("lastLoginAt"       ) var lastLoginAt       : String?                     = null,
    @SerializedName("createdAt"         ) var createdAt         : String?                     = null,
    @SerializedName("customAuth"        ) var customAuth        : Boolean?                    = null

)

data class ProviderInfo (

    @SerializedName("providerId"  ) var providerId  : String? = null,
    @SerializedName("displayName" ) var displayName : String? = null,
    @SerializedName("photoUrl"    ) var photoUrl    : String? = null,
    @SerializedName("federatedId" ) var federatedId : String? = null,
    @SerializedName("email"       ) var email       : String? = null,
    @SerializedName("rawId"       ) var rawId       : String? = null,
    @SerializedName("screenName"  ) var screenName  : String? = null

)