package com.thirteendollars.singlerent.injection.activity

import android.location.LocationManager
import com.thirteendollars.singlerent.bus.EventBus
import com.thirteendollars.singlerent.data.repository.DispositionRepository
import com.thirteendollars.singlerent.data.repository.PoiRepository
import com.thirteendollars.singlerent.data.repository.UserRepository
import com.thirteendollars.singlerent.handler.ErrorHandler
import com.thirteendollars.singlerent.injection.application.AppComponent
import com.thirteendollars.singlerent.login.LoginActivity
import com.thirteendollars.singlerent.main.MainActivity
import com.thirteendollars.singlerent.main.MarkerFabric
import com.thirteendollars.singlerent.main.map.Timer
import com.thirteendollars.singlerent.register.RegisterActivity
import com.thirteendollars.singlerent.splash.SplashScreenActivity
import dagger.Component

/**
 * Created by Damian Nowakowski on 17/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@ActivityScope
@Component(modules = [(ActivityModule::class)], dependencies = [(AppComponent::class)] )
interface ActivityComponent {
    fun inject(activity: SplashScreenActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: RegisterActivity)

    //    fun retrofit(): Retrofit
    fun bus(): EventBus
    fun userRepository(): UserRepository
//    fun sessionRepository(): SessionRepository
    fun dispositionRepository(): DispositionRepository

    fun poiRepository(): PoiRepository
    fun errorHandler(): ErrorHandler
    fun markerFabric(): MarkerFabric
    fun locationManager(): LocationManager
    fun timer(): Timer
}