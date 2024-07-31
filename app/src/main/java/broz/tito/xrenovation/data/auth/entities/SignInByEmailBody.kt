package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class SignInByEmailBody(
    @SerializedName("email") val email : String,
    @SerializedName("password") val password : String
) {
    @SerializedName("returnSecureToken") val returnSecureToken = true
}