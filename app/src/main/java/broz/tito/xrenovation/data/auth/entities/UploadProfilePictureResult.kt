package broz.tito.xrenovation.data.auth.entities

open class UploadProfilePictureResult

class PendingUploadProfilePictureResult : UploadProfilePictureResult()

class SuccessUploadProfilePictureResult(val url : String) : UploadProfilePictureResult()

class FailureUploadProfilePictureResult(val errorMessage : String) : UploadProfilePictureResult()
