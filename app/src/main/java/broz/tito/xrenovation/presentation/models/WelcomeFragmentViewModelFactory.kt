package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import javax.inject.Inject

class WelcomeFragmentViewModelFactory @Inject constructor(val sharedPrefsModel: SharedPrefsModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WelcomeFragmentViewModel(sharedPrefsModel) as T
    }
}