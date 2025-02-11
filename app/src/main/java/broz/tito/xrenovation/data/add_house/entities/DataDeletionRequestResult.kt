package broz.tito.xrenovation.data.add_house.entities

open class DataDeletionRequestResult

class PendingDataDeletionRequestResult : DataDeletionRequestResult()

class SuccessDataDeletionRequestResult(val name : String) : DataDeletionRequestResult()

class FailureDataDeletionRequestResult(val errorMessage : String) : DataDeletionRequestResult()