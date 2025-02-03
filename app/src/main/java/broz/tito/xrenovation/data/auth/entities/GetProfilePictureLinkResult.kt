package broz.tito.xrenovation.data.auth.entities

open class GetProfilePictureLinkResult

class PendingGetProfilePictureLinkResult : GetProfilePictureLinkResult()

class SuccessGetProfilePictureLinkResult(val profilePictureLink : String) : GetProfilePictureLinkResult()

class FailureGetProfilePictureLinkResult(val errorMessage : String) : GetProfilePictureLinkResult()