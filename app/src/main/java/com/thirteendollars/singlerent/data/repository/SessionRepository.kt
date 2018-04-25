package com.thirteendollars.singlerent.data.repository

import com.thirteendollars.singlerent.data.repository.local.DatabaseManager
import com.thirteendollars.singlerent.data.repository.remote.SessionService
import com.thirteendollars.singlerent.data.request.ValidateRequest
import com.thirteendollars.singlerent.data.response.ValidateResponse
import com.thirteendollars.singlerent.injection.application.AppScope
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 18/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@AppScope
class SessionRepository @Inject constructor(private val db: DatabaseManager, private val api: SessionService) {

    fun saveNewRefreshToken(token: String) = db.saveRefreshToken(token)

    fun removeRefreshToken() = db.removeRefreshToken()

    fun getRefreshToken(): String? = db.getRefreshToken()

    fun isRefreshTokenAvailable(): Boolean = db.isRefreshTokenAvailable()

    fun validateSession(): Observable<ValidateResponse> {
        val token = getRefreshToken()
        val request = ValidateRequest(token)
        return api.validate(request)
    }


    fun saveNewAccessToken(token: String) = db.saveAccessToken(token)

    fun removeAccessToken() = db.removeAccessToken()

    fun getAccessToken(): String? = db.getAccessToken()

    fun isAccessTokenAvailable(): Boolean = db.isAccessTokenAvailable()

}