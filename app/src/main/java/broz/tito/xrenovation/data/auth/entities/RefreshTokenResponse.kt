package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class RefreshTokenResponse (

    @SerializedName("expires_in"    ) var expiresIn    : String?,
    @SerializedName("token_type"    ) var tokenType    : String?,
    @SerializedName("refresh_token" ) var refreshToken : String?,
    @SerializedName("id_token"      ) var idToken      : String?,
    @SerializedName("user_id"       ) var userId       : String?,
    @SerializedName("project_id"    ) var projectId    : String?

)