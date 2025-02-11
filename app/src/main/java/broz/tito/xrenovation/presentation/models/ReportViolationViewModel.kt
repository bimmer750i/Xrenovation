package broz.tito.xrenovation.presentation.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import broz.tito.xrenovation.data.add_house.entities.FailureReportViolationResult
import broz.tito.xrenovation.data.add_house.entities.ReportViolation
import broz.tito.xrenovation.data.add_house.entities.ReportViolationResult
import broz.tito.xrenovation.data.auth.entities.FailureGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.FailureRefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessGetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.DataViolationUseCase
import broz.tito.xrenovation.domain.GetAccountInfoUseCase
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import broz.tito.xrenovation.presentation.INVALID_ID_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReportViolationViewModel @Inject constructor(val dataViolationUseCase: DataViolationUseCase,
                                                   val getAccountInfoUseCase: GetAccountInfoUseCase,
                                                   val refreshTokenUseCase: RefreshTokenUseCase,
                                                   val saveAuthResponseUseCase: SaveAuthResponseUseCase,
                                                   val sharedPrefsModel: SharedPrefsModel) : ViewModel() {

    private val _reportViolationResult = MutableLiveData<ReportViolationResult>()
    val reportViolationResult : LiveData<ReportViolationResult> = _reportViolationResult

    fun addReportViolation(context: Context,reportViolation: ReportViolation) {
        viewModelScope.launch(Dispatchers.IO) {
            reportViolation.victimLocalId = sharedPrefsModel.getLocalId(context)
            dataViolationUseCase(sharedPrefsModel.getLocalId(context),reportViolation,sharedPrefsModel.getIdToken(context)).onEach {
                _reportViolationResult.postValue(it)
            }.collect()
        }
    }

    fun resetState() {
        _reportViolationResult.postValue(ReportViolationResult())
    }

}