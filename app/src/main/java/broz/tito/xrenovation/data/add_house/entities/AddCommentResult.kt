package broz.tito.xrenovation.data.add_house.entities

open class AddCommentResult

class PendingAddCommentResult : AddCommentResult()

class SuccessAddCommentResult(val commentId : String) : AddCommentResult()

class FailureAddCommentResult(val errorMessage : String) : AddCommentResult()