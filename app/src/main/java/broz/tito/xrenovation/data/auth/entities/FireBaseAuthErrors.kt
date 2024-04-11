package broz.tito.xrenovation.data.auth.entities

import com.google.gson.annotations.SerializedName

data class FullFireBaseSignUpError(
    @SerializedName("error"    ) var error    : FireBaseSignUpError
)

data class FireBaseSignUpError (

    @SerializedName("code"    ) var code    : Int,
    @SerializedName("message" ) var message : String,
    @SerializedName("errors"  ) var errors  : ArrayList<FireBaseAuthSubError> = arrayListOf()

)

data class FireBaseAuthSubError (

    @SerializedName("message" ) var message : String,
    @SerializedName("domain"  ) var domain  : String,
    @SerializedName("reason"  ) var reason  : String

)