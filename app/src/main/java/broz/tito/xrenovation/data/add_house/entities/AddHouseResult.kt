package broz.tito.xrenovation.data.add_house.entities

open class AddHouseResult

class PendingAddHouseResult : AddHouseResult()

class SuccessAddHouseResult(val houseResponse: AddHouseResponse) : AddHouseResult()

class FailureAddHouseResult(val errorMessage : String) : AddHouseResult()