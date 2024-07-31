package broz.tito.xrenovation.domain

import android.content.Context
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import javax.inject.Inject

class SaveAuthResponseUseCase @Inject constructor(val model : SharedPrefsModel) {

    operator fun invoke(context: Context,idToken : String?, email : String?, refreshToken : String?, localId : String?) {
        idToken?.let {
            model.saveIdToken(context, it)
        }
        email?.let {
            model.saveEmailAddress(context, it)
        }
        refreshToken?.let {
            model.saveRefreshToken(context, it)
        }
        localId?.let {
            model.saveLocalId(context, it)
        }
    }

}