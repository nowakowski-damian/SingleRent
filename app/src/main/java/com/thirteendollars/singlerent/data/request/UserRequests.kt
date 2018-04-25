package com.thirteendollars.singlerent.data.request

/**
 * Created by Damian Nowakowski on 14/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
data class LoginRequest(val email: String, val password: String, val rememberMe: Boolean)
data class RegisterRequest(
        val name: String,
        val surname: String,
        val street: String,
        val number: String,
        val postalCode: String,
        val city: String,
        val pesel: String,
        val idCardNumber: String,
        val idExpiryDate: String,
        val paymentCardNumber: String,
        val paymentCardExpiryDate: String,
        val securityCode: String,
        val email: String,
        val phoneNumber: String,
        val password: String,
        val termsChecked: Boolean
)
data class ChangeUserDataRequest(
        val street: String,
        val number: String,
        val postalCode: String,
        val city: String,
        val phoneNumber: String,
        val paymentCardNumber: String?,
        val paymentCardExpiryDate: String?,
        val securityCode: String?,
        val password: String?
)