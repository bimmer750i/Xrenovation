package broz.tito.xrenovation.domain

import android.content.Context
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import javax.inject.Inject

class LogOutUseCase @Inject constructor(val sharedPrefsModel: SharedPrefsModel) {

    operator fun invoke(context: Context) {
        sharedPrefsModel.saveLocalId(context,"")
        sharedPrefsModel.saveRefreshToken(context,"")
        sharedPrefsModel.saveIdToken(context,"")
        sharedPrefsModel.saveEmailAddress(context,"")
    }

}