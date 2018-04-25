package com.thirteendollars.singlerent.main.map

import android.os.Handler
import com.thirteendollars.singlerent.injection.application.AppScope
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by Damian Nowakowski on 20/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@AppScope
class Timer @Inject constructor(private val handler: Handler) {

    private val time = PublishSubject.create<Long>()

    private var runnable: Runnable? = null

    fun start(startDate: Date): Observable<Long> {
        stop()
        runnable = object : Runnable {
            override fun run() {
                val now = Date().time
                val then = startDate.time
                val timeZone = Calendar.getInstance().timeZone.id
                val localThen = Date(then + TimeZone.getTimeZone(timeZone).getOffset(then)).time
                val duration = TimeUnit.MILLISECONDS.toSeconds(Math.max(now-localThen,0))
                time.onNext(duration)
                handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1) ) // every second
            }
        }
        handler.post(runnable)
        return time
    }


    fun stop() {
        runnable?.apply {
            handler.removeCallbacks(this)
        }
    }

}