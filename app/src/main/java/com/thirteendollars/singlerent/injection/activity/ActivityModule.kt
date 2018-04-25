package com.thirteendollars.singlerent.injection.activity

import android.app.Activity
import dagger.Module
import dagger.Provides

/**
 * Created by Damian Nowakowski on 17/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    @ActivityScope
    fun provideActivity(): Activity = activity
}