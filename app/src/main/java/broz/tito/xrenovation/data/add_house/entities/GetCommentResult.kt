package broz.tito.xrenovation.data.add_house.entities

import broz.tito.xrenovation.presentation.entities.DisplayComment
import com.google.gson.JsonObject
import org.json.JSONObject

open class GetCommentsResult

class PendingGetCommentsResult : GetCommentsResult()

class RawSuccessGetCommentsResult(val jsonObject: JsonObject) : GetCommentsResult()

class SuccessGetCommentsResult(val commentsList : ArrayList<DisplayComment>) : GetCommentsResult()

class FailureGetCommentsResult(val errorMessage : String) : GetCommentsResult()