package com.thirteendollars.singlerent.injection.application

import android.location.LocationManager
import com.thirteendollars.singlerent.App
import com.thirteendollars.singlerent.bus.EventBus
import com.thirteendollars.singlerent.data.repository.DispositionRepository
import com.thirteendollars.singlerent.data.repository.PoiRepository
import com.thirteendollars.singlerent.data.repository.SessionRepository
import com.thirteendollars.singlerent.data.repository.UserRepository
import com.thirteendollars.singlerent.handler.ErrorHandler
import com.thirteendollars.singlerent.main.MarkerFabric
import com.thirteendollars.singlerent.main.map.Timer
import dagger.Component

/**
 * Created by Damian Nowakowski on 17/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@AppScope
@Component(modules = [(AppModule::class)] )
interface AppComponent {
    fun inject(app: App)

//    fun retrofit(): Retrofit
    fun bus(): EventBus
    fun userRepository(): UserRepository
    fun sessionRepository(): SessionRepository
    fun poiRepository(): PoiRepository
    fun dispositionRepository(): DispositionRepository
    fun errorHandler(): ErrorHandler
    fun markerFabric(): MarkerFabric
    fun locationManager(): LocationManager
    fun timer(): Timer
}