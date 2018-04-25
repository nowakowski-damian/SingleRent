package com.thirteendollars.singlerent.data.model

import com.github.filosganga.geogson.model.MultiPolygon

/**
 * Created by Damian Nowakowski on 11/04/2018.
 * mail: thirteendollars.com@gmail.com
 */
data class Zone(
        val name: Type,
        val coordinates: MultiPolygon

){
    enum class Type {
        ENABLE_ZONE,
        DISABLE_ZONE
    }
}