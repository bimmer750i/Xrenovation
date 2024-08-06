package broz.tito.xrenovation.data.add_house.entities

import com.google.gson.annotations.SerializedName

data class Comment (
    @SerializedName("houseId") var houseId : String,
    @SerializedName("timeAdded") var timeAdded : Long,
    @SerializedName("displayName") val displayName : String,
    @SerializedName("localId") val localId : String,
    @SerializedName("photoUrl") val photoUrl : String,
    @SerializedName("likeCount") val likeCount : Int,
    @SerializedName("isReply") val isReply : Boolean,
    @SerializedName("repliedToCommentId") val repliedToCommentId : String,
    @SerializedName("text") val text : String) : java.io.Serializable