package com.thirteendollars.singlerent.main

import com.thirteendollars.singlerent.data.model.Disposition
import com.thirteendollars.singlerent.injection.activity.ActivityScope
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 21/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@ActivityScope
class MainViewModel @Inject constructor(){


    var disposition: Disposition? = null
    val events = PublishSubject.create<MainEvent>()



}


sealed class MainEvent{
}

