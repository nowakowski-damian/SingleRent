package com.thirteendollars.singlerent.data.repository.remote

import com.thirteendollars.singlerent.data.request.ValidateRequest
import com.thirteendollars.singlerent.data.response.ValidateResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Damian Nowakowski on 18/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
interface SessionService {

    companion object {
        const val VALIDATE_URL = "/user/validate"
    }

    @POST(VALIDATE_URL)
    fun validate(@Body tokenRequest: ValidateRequest): Observable<ValidateResponse>
}