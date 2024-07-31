package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

class SendPasswordResetEmailBody(@SerializedName("email") val email : String) {
    @SerializedName("requestType") val requestType : String = "PASSWORD_RESET"
}