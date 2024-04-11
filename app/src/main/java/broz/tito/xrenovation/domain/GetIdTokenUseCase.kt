package broz.tito.xrenovation.domain

import android.content.Context
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import javax.inject.Inject

class GetIdTokenUseCase @Inject constructor(val model: SharedPrefsModel) {

    operator fun invoke(context: Context) : String {
        return model.getIdToken(context)
    }

}