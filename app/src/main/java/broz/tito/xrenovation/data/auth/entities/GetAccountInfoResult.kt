package broz.tito.xrenovation.data.auth.entities

open class GetAccountInfoResult

class PendingGetAccountInfoResult : GetAccountInfoResult()

class SuccessGetAccountInfoResult(val user : User) : GetAccountInfoResult()

class FailureGetAccountInfoResult(val errorMessage : String) : GetAccountInfoResult()