package broz.tito.xrenovation.data.sharedprefs

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsModel {

    private val PREFS_NAME = "SharedPrefsModel"

    private val ID_TOKEN = "ID_TOKEN"

    private val EMAIL = "EMAIL"

    private val REFRESH_TOKEN = "REFRESH_TOKEN"

    private val LOCAL_ID = "LOCAL_ID"

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
    }

    fun saveIdToken(context: Context, idToken : String) {
        getSharedPrefs(context).edit().putString(ID_TOKEN,idToken).apply()
    }

    fun saveEmailAddress(context: Context,email : String) {
        getSharedPrefs(context).edit().putString(EMAIL,email).apply()
    }

    fun saveRefreshToken(context: Context,refreshToken : String) {
        getSharedPrefs(context).edit().putString(REFRESH_TOKEN,refreshToken).apply()
    }

    fun saveLocalId(context: Context, localId : String) {
        getSharedPrefs(context).edit().putString(LOCAL_ID,localId).apply()
    }

    fun getIdToken(context: Context) : String {
        return getSharedPrefs(context).getString(ID_TOKEN,"") ?: ""
    }

    fun getEmailAddress(context: Context) : String {
        return getSharedPrefs(context).getString(EMAIL,"") ?: ""
    }

    fun getRefreshToken(context: Context) : String {
        return getSharedPrefs(context).getString(REFRESH_TOKEN,"") ?: ""
    }

    fun getLocalId(context: Context) : String {
        return getSharedPrefs(context).getString(LOCAL_ID,"") ?: ""
    }


}