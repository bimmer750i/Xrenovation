package broz.tito.xrenovation.data.auth.entities

open class VerifyEmailResult

class PendingVerifyEmailResult : VerifyEmailResult()

class SuccessVerifyEmailResult(val response: VerifyEmailResponse) : VerifyEmailResult()

class FailureVerifyEmailResult(val errorMessage : String) : VerifyEmailResult()

