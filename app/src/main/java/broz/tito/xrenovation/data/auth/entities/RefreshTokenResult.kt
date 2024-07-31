package broz.tito.xrenovation.data.auth.entities

open class RefreshTokenResult

class PendingRefreshTokenResult : RefreshTokenResult()

class SuccessRefreshTokenResult(val response: RefreshTokenResponse) : RefreshTokenResult()

class FailureRefreshTokenResult(val errorMessage : String) : RefreshTokenResult()