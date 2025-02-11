package broz.tito.xrenovation.domain

import broz.tito.xrenovation.data.add_house.entities.LoadPhotosResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadPhotosToFireBaseUseCase @Inject constructor(val repository: AddHouseRepository) {

    operator fun invoke(localId: String,path : String, list : ArrayList<String>,accessToken : String) : Flow<LoadPhotosResult> {
        return repository.loadPhotosToFireBase(localId,path,list,accessToken)
    }

}