package broz.tito.xrenovation.data.sharedprefs

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "SharedPrefsModel"

private const val ID_TOKEN = "ID_TOKEN"

private const val EMAIL = "EMAIL"

private const val REFRESH_TOKEN = "REFRESH_TOKEN"

private const val LOCAL_ID = "LOCAL_ID"

private const val FIRST_START = "FIRST_START"

class SharedPrefsModel {

    private fun getSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
    }

    fun isFirstStart(context: Context) : Boolean = getSharedPrefs(context).getBoolean(FIRST_START,true)

    fun setFirstStartCompleted(context: Context) = getSharedPrefs(context).edit().putBoolean(FIRST_START,false).apply()

    fun saveIdToken(context: Context, idToken : String) = getSharedPrefs(context).edit().putString(ID_TOKEN,idToken).apply()

    fun saveEmailAddress(context: Context,email : String) = getSharedPrefs(context).edit().putString(EMAIL,email).apply()

    fun saveRefreshToken(context: Context,refreshToken : String) = getSharedPrefs(context).edit().putString(REFRESH_TOKEN,refreshToken).apply()

    fun saveLocalId(context: Context, localId : String) = getSharedPrefs(context).edit().putString(LOCAL_ID,localId).apply()

    fun getIdToken(context: Context) : String = getSharedPrefs(context).getString(ID_TOKEN,"") ?: ""

    fun getEmailAddress(context: Context) : String = getSharedPrefs(context).getString(EMAIL,"") ?: ""

    fun getRefreshToken(context: Context) : String = getSharedPrefs(context).getString(REFRESH_TOKEN,"") ?: ""

    fun getLocalId(context: Context) : String = getSharedPrefs(context).getString(LOCAL_ID,"") ?: ""


}