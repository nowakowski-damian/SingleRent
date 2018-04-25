package com.thirteendollars.singlerent.data.repository

import com.google.android.gms.maps.model.LatLng
import com.thirteendollars.singlerent.data.model.Vehicle
import com.thirteendollars.singlerent.data.model.Zone
import com.thirteendollars.singlerent.data.repository.remote.PoiService
import com.thirteendollars.singlerent.injection.application.AppScope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 11/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@AppScope
class PoiRepository @Inject constructor(private val api: PoiService ) {

    fun getVehicles(): Observable< MutableList<Vehicle> > =  api.getVehicles()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.vehicles }

    fun getNearestVehicle(userLocation: LatLng): Observable< Vehicle > =  api.getNearestVehicle(userLocation.latitude, userLocation.longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.nearestVehicle }

    fun getParkingZones(): Observable< MutableList<Zone> > = api.getParkingZones()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { mutableListOf(
                    Zone(Zone.Type.ENABLE_ZONE,it.enableParkingZones),
                    Zone(Zone.Type.DISABLE_ZONE,it.disableParkingZones)
            )
            }
}