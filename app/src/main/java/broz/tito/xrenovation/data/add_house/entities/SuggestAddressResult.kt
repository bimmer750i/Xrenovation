package broz.tito.xrenovation.data.add_house.entities

import com.yandex.mapkit.search.SuggestItem

open class SuggestAddressResult

class PendingSuggestAddressResult() : SuggestAddressResult()

class SuccessSuggestAddressResult(val list : MutableList<SuggestItem>) : SuggestAddressResult()

class FailureSuggestAddressResult(val errorMessage : String) : SuggestAddressResult()

