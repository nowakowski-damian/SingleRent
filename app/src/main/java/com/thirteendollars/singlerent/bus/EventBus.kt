package com.thirteendollars.singlerent.bus

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class EventBus: IBus {

    var pipe: PublishSubject<Any> = PublishSubject.create()

    override fun getPipe(): Observable<Any> {
        return pipe
    }

    override fun post(action: Any) {
        pipe.onNext(action)
    }

}