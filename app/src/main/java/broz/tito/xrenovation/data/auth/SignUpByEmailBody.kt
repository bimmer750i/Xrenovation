package broz.tito.xrenovation.data.auth

import com.google.gson.annotations.SerializedName

open class SignUpByEmailBody(
    @SerializedName("email"             ) var email             : String?,
    @SerializedName("password"          ) var password          : String?,
    @SerializedName("returnSecureToken" ) var returnSecureToken : Boolean?)