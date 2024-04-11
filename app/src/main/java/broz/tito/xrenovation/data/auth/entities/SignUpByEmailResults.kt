package broz.tito.xrenovation.data.auth.entities

open class SignUpByEmailResult

class PendingSignUpByEmailResult : SignUpByEmailResult()

class SuccessSignUpByEmailResult(val result : RawSignUpByEmailResponse) : SignUpByEmailResult()

class FailureSignUpByEmailResult(val errorMessage : String) : SignUpByEmailResult()