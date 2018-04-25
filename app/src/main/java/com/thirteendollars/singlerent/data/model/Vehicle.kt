package com.thirteendollars.singlerent.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Damian Nowakowski on 10/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

data class Vehicle(
        val id: Int,
        val name: String,
        val vin: String,
        val latitude: Double,
        val longitude: Double,
        val rangeKm: Double?,
        val registrationNumber: String?,
        val type: Type
) {

    enum class Type {
        @SerializedName("TYPE_BICYCLE") BICYCLE,
        @SerializedName("TYPE_MOTORCYCLE") MOTORCYCLE
    }
}