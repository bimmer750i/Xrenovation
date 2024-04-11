package broz.tito.xrenovation.data.auth.entities

open class CaptchaResult

class PendingCaptchaResult : CaptchaResult()

class SuccessCaptchaResult(val response: CaptchaResponse) : CaptchaResult()

class FailureCaptchaResult() : CaptchaResult()

