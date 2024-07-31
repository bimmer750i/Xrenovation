package broz.tito.xrenovation.data.auth.entities

open class SendPasswordResetEmailResult

class PendingSendPasswordResetEmailResult : SendPasswordResetEmailResult()

class SuccessSendPasswordResetEmailResult(val email : String) : SendPasswordResetEmailResult()

class FailureSendPasswordResetEmailResult(val errorMessage : String) : SendPasswordResetEmailResult()