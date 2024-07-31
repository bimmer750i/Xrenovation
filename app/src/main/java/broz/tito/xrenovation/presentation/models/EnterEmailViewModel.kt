package broz.tito.xrenovation.presentation.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.SendPasswordResetEmailResult
import broz.tito.xrenovation.domain.SendEmailVerificationCodeUseCase
import broz.tito.xrenovation.domain.SendPasswordResetEmailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class EnterEmailViewModel @Inject constructor(val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase) : ViewModel() {

    val _sendPasswordResetEmailResult  = MutableLiveData<SendPasswordResetEmailResult>()
    val sendPasswordResetEmailResult : LiveData<SendPasswordResetEmailResult> = _sendPasswordResetEmailResult

    fun sendPasswordResetEmail(email : String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendPasswordResetEmailUseCase(email).onEach {
                _sendPasswordResetEmailResult.postValue(it)
            }.collect()
        }
    }

}