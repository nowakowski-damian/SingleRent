package com.thirteendollars.singlerent

import android.app.Application
import com.thirteendollars.singlerent.injection.application.AppComponent
import com.thirteendollars.singlerent.injection.application.AppModule
import com.thirteendollars.singlerent.injection.application.DaggerAppComponent
import timber.log.Timber

/**
 * Created by Damian Nowakowski on 17/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
class App: Application() {


    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}