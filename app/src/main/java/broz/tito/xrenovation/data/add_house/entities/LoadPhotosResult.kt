package broz.tito.xrenovation.data.add_house.entities

open class LoadPhotosResult

class PendingLoadPhotosResult : LoadPhotosResult()

class SuccessLoadPhotosResult(val urlList : ArrayList<String>) : LoadPhotosResult()

class FailureLoadPhotosResult(val errorMessage : String) : LoadPhotosResult()