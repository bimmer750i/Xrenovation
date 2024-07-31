package broz.tito.xrenovation.data.auth.entities

open class SetAccountInfoResult

class PendingSetAccountInfoResult : SetAccountInfoResult()

class SuccessSetAccountInfoResult(val response: SetAccountInfoResponse) : SetAccountInfoResult()

class FailureSetAccountInfoResult(val errorMessage : String) : SetAccountInfoResult()