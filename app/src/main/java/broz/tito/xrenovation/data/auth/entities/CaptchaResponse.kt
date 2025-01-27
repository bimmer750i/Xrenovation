package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

data class CaptchaResponse (@SerializedName("status"  ) var status  : String?,
                            @SerializedName("message" ) var message : String?)