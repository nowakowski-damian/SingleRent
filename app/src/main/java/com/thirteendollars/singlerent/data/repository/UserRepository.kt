package com.thirteendollars.singlerent.data.repository

import com.thirteendollars.singlerent.data.repository.remote.UserService
import com.thirteendollars.singlerent.data.request.ChangeUserDataRequest
import com.thirteendollars.singlerent.data.request.LoginRequest
import com.thirteendollars.singlerent.data.request.RegisterRequest
import com.thirteendollars.singlerent.data.response.UserResponse
import com.thirteendollars.singlerent.injection.application.AppScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 14/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@AppScope
class UserRepository @Inject constructor( private val api: UserService){

    fun login(email: String, password: String, rememberMe: Boolean): Observable<UserResponse> {
        val request = LoginRequest(email, password, rememberMe)
        return api.login(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun logout(): Completable =  api.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    fun getCurrentUser(): Observable<UserResponse> = api.getCurrentUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun register(
            name: String,
            surname: String,
            street: String,
            number: String,
            postalCode: String,
            city: String,
            pesel: String,
            idCardNumber: String,
            idExpiryDate: String,
            paymentCardNumber: String,
            paymentCardExpiryDate: String,
            securityCode: String,
            email: String,
            phoneNumber: String,
            password: String,
            termsChecked: Boolean
    ): Completable {
        val request = RegisterRequest(
                name,
                surname,
                street,
                number,
                postalCode,
                city,
                pesel,
                idCardNumber,
                idExpiryDate,
                paymentCardNumber,
                paymentCardExpiryDate,
                securityCode,
                email,
                phoneNumber,
                password,
                termsChecked
        )
        return api.register(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun changeUserData(
            street: String,
            number: String,
            postalCode: String,
            city: String,
            phoneNumber: String,
            paymentCardNumber: String? = null,
            paymentCardExpiryDate: String? = null,
            securityCode: String? = null,
            password: String? = null
    ): Completable {
        val request = ChangeUserDataRequest(
                street,
                number,
                postalCode,
                city,
                phoneNumber,
                paymentCardNumber,
                paymentCardExpiryDate,
                securityCode,
                password
        )
        return api.changeUserData(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}