package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class VerifyEmailBody( @SerializedName("idToken") val idToken : String) {
    @SerializedName("requestType") val requestType = "VERIFY_EMAIL"
}