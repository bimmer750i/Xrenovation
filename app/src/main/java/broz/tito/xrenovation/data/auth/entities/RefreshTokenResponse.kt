package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class RefreshTokenResponse (

    @SerializedName("expires_in"    ) var expiresIn    : String? = null,
    @SerializedName("token_type"    ) var tokenType    : String? = null,
    @SerializedName("refresh_token" ) var refreshToken : String? = null,
    @SerializedName("id_token"      ) var idToken      : String? = null,
    @SerializedName("user_id"       ) var userId       : String? = null,
    @SerializedName("project_id"    ) var projectId    : String? = null

)