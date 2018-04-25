package com.thirteendollars.singlerent.data.response

import com.github.filosganga.geogson.model.MultiPolygon
import com.thirteendollars.singlerent.data.model.Vehicle

/**
 * Created by Damian Nowakowski on 20/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

data class VehicleResponse(
        val vehicles: MutableList<Vehicle>
)

data class NearestVehicleResponse(
        val nearestVehicle: Vehicle
)

data class ParkingZonesResponse(
        val enableParkingZones: MultiPolygon,
        val disableParkingZones: MultiPolygon
)