package com.thirteendollars.singlerent.data.repository.remote

import com.github.filosganga.geogson.gson.GeometryAdapterFactory
import com.google.gson.GsonBuilder
import com.thirteendollars.singlerent.injection.application.AppScope
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 11/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@AppScope
class GsonFactory @Inject constructor(){

    fun create() = GsonConverterFactory.create( getCustomGsonFormat() )

    private fun getCustomGsonFormat() = GsonBuilder()
            .registerTypeAdapterFactory(GeometryAdapterFactory())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()

}