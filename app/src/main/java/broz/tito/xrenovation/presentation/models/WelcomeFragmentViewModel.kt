package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.ViewModel
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import javax.inject.Inject

class WelcomeFragmentViewModel @Inject constructor(private val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    fun setFirstStartCompleted(context: Context) {
        sharedPrefsModel.setFirstStartCompleted(context)
    }

}