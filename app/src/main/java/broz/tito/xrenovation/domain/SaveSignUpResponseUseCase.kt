package broz.tito.xrenovation.domain

import android.content.Context
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import javax.inject.Inject

class SaveSignUpResponseUseCase @Inject constructor(val model : SharedPrefsModel) {

    operator fun invoke(context: Context,idToken : String, email : String, refreshToken : String, localId : String) {
        model.saveIdToken(context, idToken)
        model.saveEmailAddress(context, email)
        model.saveRefreshToken(context, refreshToken)
        model.saveLocalId(context, localId)
    }

}