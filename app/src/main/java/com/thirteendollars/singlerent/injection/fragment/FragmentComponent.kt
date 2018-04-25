package com.thirteendollars.singlerent.injection.application

import com.thirteendollars.singlerent.injection.activity.ActivityComponent
import com.thirteendollars.singlerent.main.account.AccountFragment
import com.thirteendollars.singlerent.main.map.MapFragment
import dagger.Component

/**
 * Created by Damian Nowakowski on 17/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@FragmentScope
@Component(modules = [(FragmentModule::class)], dependencies = [(ActivityComponent::class)] )
interface FragmentComponent {
    fun inject(mapFragment: MapFragment)
    fun inject(accountFragment: AccountFragment)
}