package com.thirteendollars.singlerent.splash

import android.databinding.ObservableField
import com.thirteendollars.singlerent.data.repository.DispositionRepository
import com.thirteendollars.singlerent.data.repository.UserRepository
import com.thirteendollars.singlerent.data.response.UserResponse
import com.thirteendollars.singlerent.handler.ErrorHandler
import com.thirteendollars.singlerent.injection.activity.ActivityScope
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 14/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@ActivityScope
class SplashScreenViewModel @Inject constructor(
        private val userRepository: UserRepository,
        private val dispositionRepository: DispositionRepository,
        private val errorHandler: ErrorHandler
) {

    val events = PublishSubject.create<SplashEvent>()
    val isLoading: ObservableField<Boolean> = ObservableField(false)

    fun checkCurrentUser() {
        isLoading.set(true)
        userRepository.getCurrentUser()
                .subscribeWith( object: DisposableObserver<UserResponse>() {
                    override fun onComplete() {
                        isLoading.set(false)
                    }

                    override fun onNext(value: UserResponse?) {
                        dispositionRepository.setCurrentDisposition(value?.currentDisposition)
                        val event = SplashEvent.AuthenticationSuccess()
                        events.onNext(event)
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        isLoading.set(false)
                        val message = errorHandler.getError(e).message.orEmpty()
                        val event = SplashEvent.AuthenticationFailure(message)
                        events.onNext(event)
                    }
                })
    }
}

sealed class SplashEvent {
    data class AuthenticationFailure(val errorMessage: String) : SplashEvent()
    class AuthenticationSuccess : SplashEvent()
}