package com.thirteendollars.singlerent.login

import android.databinding.ObservableField
import com.thirteendollars.singlerent.data.model.Disposition
import com.thirteendollars.singlerent.data.repository.SessionRepository
import com.thirteendollars.singlerent.data.repository.UserRepository
import com.thirteendollars.singlerent.data.response.UserResponse
import com.thirteendollars.singlerent.handler.ErrorHandler
import com.thirteendollars.singlerent.injection.activity.ActivityScope
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 21/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
@ActivityScope
class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository,
        private val sessionRepository: SessionRepository,
        private val errorHandler: ErrorHandler
) {

    val events = PublishSubject.create<LoginEvent>()

    val login = ObservableField<String>()
    val password = ObservableField<String>()
    val rememberMe = ObservableField<Boolean>(false)

    fun onLoginButton() {
        if( areCredentialsValid() ) {
            events.onNext(LoginEvent.LoginStarted())
            userRepository.login(login.get()!!,password.get()!!, rememberMe.get()!!)
                    .subscribeWith( object : DisposableObserver<UserResponse>() {
                        override fun onComplete() {
                        }

                        override fun onNext(response: UserResponse) {
                            sessionRepository.saveNewRefreshToken( response.refreshToken.orEmpty() )
                            sessionRepository.saveNewAccessToken( response.accessToken.orEmpty() )
                            events.onNext(LoginEvent.LoginSuccess(response.currentDisposition))
                        }

                        override fun onError(e: Throwable?) {
                            e?.printStackTrace()
                            val message = errorHandler.getError(e).message.orEmpty()
                            events.onNext(LoginEvent.LoginFailure(message))
                        }
                    })
        } else {
            events.onNext(LoginEvent.ValidationFailure())
        }
    }

    private fun areCredentialsValid(): Boolean = !login.get().isNullOrEmpty() && !password.get().isNullOrEmpty()

    fun onRegisterButton() {
        events.onNext(LoginEvent.RegisterScreen())
    }

}


sealed class LoginEvent {
    class LoginStarted: LoginEvent()
    data class LoginSuccess( val currentDisposition: Disposition?): LoginEvent()
    data class LoginFailure( val errorMessage: String): LoginEvent()
    class ValidationFailure: LoginEvent()
    class RegisterScreen: LoginEvent()
}