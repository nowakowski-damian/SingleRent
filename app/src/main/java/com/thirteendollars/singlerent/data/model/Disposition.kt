package com.thirteendollars.singlerent.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Damian Nowakowski on 14/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
data class Disposition(
        val type: Type,
        var vehicle: Vehicle,
        var startDate: String,
        var endDate: String?,
        val distanceToVehicle: Double?, //km
        val walkTimeToVehicle: Int?,  //min
        val costPerMinute: Double?
): Serializable {
    enum class Type {
        @SerializedName("TYPE_RESERVATION") RESERVATION,
        @SerializedName("TYPE_RENTAL") RENTAL
    }
}