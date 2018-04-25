package com.thirteendollars.singlerent.data.model

/**
 * Created by Damian Nowakowski on 14/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
data class User(
        var name: String,
        var surname: String,
        var email: String,
        var password: String?,
        var street: String,
        var number: String,
        var postalCode: String,
        var city: String,
        var paymentCardNumber: String,
        var paymentCardExpiryDate: String,
        var securityCode: String,
        var phoneNumber: String
)