package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class StartFragmentViewModel @Inject constructor(val useCase: RefreshTokenUseCase, val saveAuthResponseUseCase: SaveAuthResponseUseCase, val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    private val _refreshTokenResult = MutableLiveData<RefreshTokenResult>()
    val refreshTokenResult : LiveData<RefreshTokenResult> = _refreshTokenResult

    fun refreshToken(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase(sharedPrefsModel.getRefreshToken(context)).onEach {
                if (it is SuccessRefreshTokenResult) {
                    saveAuthResponseUseCase(context,it.response.idToken,null,it.response.refreshToken,null)
                }
                _refreshTokenResult.postValue(it)
            }.collect()
        }
    }

    fun isFirstStart(context: Context) : Boolean = sharedPrefsModel.isFirstStart(context)

    fun setFirstStartCompleted(context: Context) = sharedPrefsModel.setFirstStartCompleted(context)

}