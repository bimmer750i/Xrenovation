package broz.tito.xrenovation.data.add_house.entities

open class AddHousePointResult

class PendingAddHousePointResult : AddHousePointResult()

class SuccessAddHousePointResult(val housePointResponse: AddHousePointResponse) : AddHousePointResult()

class FailureAddHousePointResult(val errorMessage : String) : AddHousePointResult()
