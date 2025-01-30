package broz.tito.xrenovation.presentation.models

import android.content.Context
import broz.tito.xrenovation.data.auth.entities.GetAccountInfoResult
import broz.tito.xrenovation.data.auth.entities.RefreshTokenResult
import broz.tito.xrenovation.data.auth.entities.SuccessRefreshTokenResult
import broz.tito.xrenovation.data.sharedprefs.SharedPrefsModel
import broz.tito.xrenovation.domain.GetAccountInfoUseCase
import broz.tito.xrenovation.domain.RefreshTokenUseCase
import broz.tito.xrenovation.domain.SaveAuthResponseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach

suspend fun refreshToken(context: Context,refreshTokenUseCase : RefreshTokenUseCase,
                                 sharedPrefsModel : SharedPrefsModel,
                                 saveAuthResponseUseCase: SaveAuthResponseUseCase) : RefreshTokenResult = coroutineScope {
                                     async(Dispatchers.IO) {
    return@async refreshTokenUseCase(sharedPrefsModel.getRefreshToken(context)).onEach {
        if (it is SuccessRefreshTokenResult) {
            saveAuthResponseUseCase(
                context,
                it.response.idToken,
                null,
                it.response.refreshToken,
                null
            )
        }
    }.last()
}.await()
}

suspend fun getAccountInfo(context: Context, getAccountInfoUseCase : GetAccountInfoUseCase,sharedPrefsModel: SharedPrefsModel)
: GetAccountInfoResult = coroutineScope { async {
    return@async getAccountInfoUseCase(sharedPrefsModel.getIdToken(context)).last()
}.await()
}