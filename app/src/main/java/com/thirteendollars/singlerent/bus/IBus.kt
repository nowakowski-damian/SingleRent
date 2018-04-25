package com.thirteendollars.singlerent.bus

import io.reactivex.Observable

interface IBus {
    fun post(action: Any)
    fun getPipe(): Observable<Any>
}