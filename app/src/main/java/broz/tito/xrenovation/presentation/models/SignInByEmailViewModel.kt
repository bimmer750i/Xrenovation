package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.SignInByEmailResult
import broz.tito.xrenovation.data.auth.entities.SuccessSignInByEmailResult
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import broz.tito.xrenovation.domain.SignInByEmailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInByEmailViewModel @Inject constructor(val useCase: SignInByEmailUseCase, val saveResponseUseCase: SaveAuthResponseUseCase) : ViewModel() {

    private val TAG = "SignInByEmailViewMode"

    private val _signInResult = MutableLiveData<SignInByEmailResult>()
    val signInResult : LiveData<SignInByEmailResult> = _signInResult

    fun signInByEmail(context : Context, email : String, password : String) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase(email, password).onEach {
                if (it is SuccessSignInByEmailResult && it.response.registered) {
                    saveResponseUseCase(context,it.response.idToken,it.response.email,it.response.refreshToken,it.response.localId)
                }
                _signInResult.postValue(it)
            }.collect()
        }
    }


}