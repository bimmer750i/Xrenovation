package broz.tito.xrenovation.data.add_house.entities

open class AddHouseCorrectionResult

class PendingAddHouseCorrectionResult : AddHouseCorrectionResult()

class SuccessAddHouseCorrectionResult(val houseCorrectionId : String) : AddHouseCorrectionResult()

class FailureAddHouseCorrectionResult(val errorMessage : String) : AddHouseCorrectionResult()