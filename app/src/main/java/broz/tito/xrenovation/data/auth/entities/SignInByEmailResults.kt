package broz.tito.xrenovation.data.auth.entities

open class SignInByEmailResult

class PendingSignInByEmailResult() : SignInByEmailResult()

class SuccessSignInByEmailResult(val response: RawSignInByEmailResponse) : SignInByEmailResult()

class FailureSignInByEmailResult(val errorMessage : String) : SignInByEmailResult()