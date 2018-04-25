package com.thirteendollars.singlerent.main

import android.content.Context
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.thirteendollars.singlerent.R
import com.thirteendollars.singlerent.data.model.Vehicle
import com.thirteendollars.singlerent.data.model.Zone
import com.thirteendollars.singlerent.injection.application.AppScope
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 12/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@AppScope
class MarkerFabric @Inject constructor(){

    fun createVehicle(vehicle: Vehicle): MarkerOptions {
        val markerOptions = MarkerOptions()
        when(vehicle.type) {
            Vehicle.Type.BICYCLE -> {
                markerOptions.icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.pin_bike)
                )
            }
            Vehicle.Type.MOTORCYCLE -> {
                markerOptions.icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.pin_motorcycle)
                )
            }
        }
        markerOptions
                .title(vehicle.name)
                .position( LatLng(vehicle.latitude, vehicle.longitude) )

        return markerOptions
    }

    fun createZonePolygon(zoneType: Zone.Type,
                          points: MutableList<LatLng>,
                          context: Context): PolygonOptions {
        val polygonOptions = PolygonOptions()
        val fillColor: Int
        val strokeColor: Int
        when(zoneType) {
            Zone.Type.ENABLE_ZONE -> {
                fillColor = R.color.enableZoneFill
                strokeColor = R.color.enableZoneStroke
            }
            Zone.Type.DISABLE_ZONE -> {
                fillColor = R.color.disableZoneFill
                strokeColor = R.color.disableZoneStroke
            }
        }
        polygonOptions
            .strokeColor(ContextCompat.getColor(context, strokeColor))
            .fillColor(ContextCompat.getColor(context, fillColor))
            .addAll(points)

        return polygonOptions
    }

}