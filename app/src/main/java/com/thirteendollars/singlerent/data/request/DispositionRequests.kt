package com.thirteendollars.singlerent.data.request

/**
 * Created by Damian Nowakowski on 19/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

data class StartReservationRequest(val vehicleId: Int,val latitude: Double, val longitude: Double)
data class StartRentalRequest(val nfcCode:String)
