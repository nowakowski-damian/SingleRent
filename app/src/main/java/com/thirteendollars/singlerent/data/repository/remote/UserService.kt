package com.thirteendollars.singlerent.data.repository.remote

import com.thirteendollars.singlerent.data.request.ChangeUserDataRequest
import com.thirteendollars.singlerent.data.request.LoginRequest
import com.thirteendollars.singlerent.data.request.RegisterRequest
import com.thirteendollars.singlerent.data.response.UserResponse
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by Damian Nowakowski on 14/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
interface UserService {

    companion object {
        const val LOGIN_URL = "/user/login"
    }

    @GET("/user")
    fun getCurrentUser(): Observable<UserResponse>

    @POST("/user")
    fun changeUserData(@Body changeUserDataRequest: ChangeUserDataRequest): Completable

    @POST(LOGIN_URL)
    fun login(@Body loginRequest: LoginRequest): Observable<UserResponse>

    @POST("/user/logout")
    fun logout(): Completable

    @POST("/register")
    fun register(@Body registerRequest: RegisterRequest): Completable

}