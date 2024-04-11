package broz.tito.xrenovation.data.auth

import com.google.gson.annotations.SerializedName

open class SignUpByEmailBody(
    @SerializedName("email"             ) var email             : String?  = null,
    @SerializedName("password"          ) var password          : String?  = null,
    @SerializedName("returnSecureToken" ) var returnSecureToken : Boolean? = null)