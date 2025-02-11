package broz.tito.xrenovation.data.add_house.entities

import com.google.gson.annotations.SerializedName

class ReportViolation(
    @SerializedName("commentId")
    val commentId : String,
    @SerializedName("criminalLocalId")
    val criminalLocalId : String,
    @SerializedName("victimLocalId")
    var victimLocalId : String,
    @SerializedName("reportText")
    val reportText : String
)