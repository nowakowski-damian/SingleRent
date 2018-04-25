package com.thirteendollars.singlerent.data.repository.local

import com.securepreferences.SecurePreferences
import com.thirteendollars.singlerent.injection.application.AppScope
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 18/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@AppScope
 class DatabaseManager @Inject constructor(private val preferences: SecurePreferences) {

    fun getRefreshToken(): String? = preferences.getString(Preference.REFRESH_TOKEN.key,null)
    fun saveRefreshToken(token: String) = preferences.edit().putString(Preference.REFRESH_TOKEN.key, token).apply()
    fun removeRefreshToken() = preferences.edit().remove(Preference.REFRESH_TOKEN.key).apply()
    fun isRefreshTokenAvailable(): Boolean = !getRefreshToken().isNullOrEmpty()

    fun getAccessToken(): String? = preferences.getString(Preference.ACCESS_TOKEN.key,null)
    fun saveAccessToken(token: String) = preferences.edit().putString(Preference.ACCESS_TOKEN.key, token).apply()
    fun removeAccessToken() = preferences.edit().remove(Preference.ACCESS_TOKEN.key).apply()
    fun isAccessTokenAvailable(): Boolean = !getAccessToken().isNullOrEmpty()

}

private enum class Preference(val key: String) {
    REFRESH_TOKEN("refresh_token"),
    ACCESS_TOKEN("access_token")
}