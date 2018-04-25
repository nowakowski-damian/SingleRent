package com.thirteendollars.singlerent.data.repository.remote

import com.thirteendollars.singlerent.data.response.NearestVehicleResponse
import com.thirteendollars.singlerent.data.response.ParkingZonesResponse
import com.thirteendollars.singlerent.data.response.VehicleResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Damian Nowakowski on 11/04/2018.
 * mail: thirteendollars.com@gmail.com
 */
interface PoiService {

    @GET("/vehicles")
    fun getVehicles(): Observable<VehicleResponse>

    @GET("/vehicles/nearest")
    fun getNearestVehicle(@Query("lat") latitude: Double, @Query("lon") longitude: Double ): Observable<NearestVehicleResponse>


    @GET("/parkingZones")
    fun getParkingZones(): Observable<ParkingZonesResponse>
}