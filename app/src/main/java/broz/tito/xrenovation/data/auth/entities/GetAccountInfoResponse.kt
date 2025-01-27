package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class GetAccountInfoResponse (

    @SerializedName("users" ) val users : ArrayList<User>?

)

data class User (

    @SerializedName("localId"           ) val localId           : String?                     ,
    @SerializedName("email"             ) val email             : String?                     ,
    @SerializedName("emailVerified"     ) val emailVerified     : Boolean?                    ,
    @SerializedName("displayName"       ) val displayName       : String?                     ,
    @SerializedName("providerUserInfo"  ) val providerUserInfo  : ArrayList<ProviderInfo>? = arrayListOf(),
    @SerializedName("photoUrl"          ) val photoUrl          : String?                     ,
    @SerializedName("passwordHash"      ) val passwordHash      : String?                     ,
    @SerializedName("passwordUpdatedAt" ) val passwordUpdatedAt : String?                        ,
    @SerializedName("validSince"        ) val validSince        : String?                     ,
    @SerializedName("disabled"          ) val disabled          : Boolean?                    ,
    @SerializedName("lastLoginAt"       ) val lastLoginAt       : String?                     ,
    @SerializedName("createdAt"         ) val createdAt         : String?                     ,
    @SerializedName("customAuth"        ) val customAuth        : Boolean?

)

data class ProviderInfo (

    @SerializedName("providerId"  ) val providerId  : String? ,
    @SerializedName("displayName" ) val displayName : String? ,
    @SerializedName("photoUrl"    ) val photoUrl    : String? ,
    @SerializedName("federatedId" ) val federatedId : String? ,
    @SerializedName("email"       ) val email       : String? ,
    @SerializedName("rawId"       ) val rawId       : String? ,
    @SerializedName("screenName"  ) val screenName  : String?

)