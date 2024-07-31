package broz.tito.xrenovation.domain

import android.content.Context
import broz.tito.xrenovation.data.auth.entities.UploadProfilePictureResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class UploadProfilePictureUseCase @Inject constructor(val emailRepository: broz.tito.xrenovation.domain.EmailRepository, val sharedPrefsModel: SharedPrefsModel) {

    suspend operator fun invoke(context: Context,file: File) : Flow<UploadProfilePictureResult> {
        return emailRepository.uploadProfilePicture(sharedPrefsModel.getLocalId(context),file)
    }

}