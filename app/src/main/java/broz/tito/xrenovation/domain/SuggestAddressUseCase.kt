package broz.tito.xrenovation.domain

import android.content.Context
import broz.tito.xrenovation.data.add_house.entities.SuggestAddressResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SuggestAddressUseCase @Inject constructor(val repository: broz.tito.xrenovation.domain.AddHouseRepository) {

    operator fun invoke(context: Context,address : String) : Flow<SuggestAddressResult> {
        return repository.suggestAddress(context, address)
    }

}