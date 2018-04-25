package com.thirteendollars.singlerent.register

import android.databinding.ObservableField
import com.thirteendollars.singlerent.data.repository.UserRepository
import com.thirteendollars.singlerent.handler.ErrorHandler
import com.thirteendollars.singlerent.injection.activity.ActivityScope
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 20/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@ActivityScope
class RegisterViewModel @Inject constructor(
        private val userRepository: UserRepository,
        private val errorHandler: ErrorHandler
        ){

    val events = PublishSubject.create<RegisterEvents>()

    val isLoading: ObservableField<Boolean> = ObservableField(false)

    val name = ObservableField<String>()
    val surname = ObservableField<String>()
    val street = ObservableField<String>()
    val number = ObservableField<String>()
    val postalCode = ObservableField<String>()
    val city = ObservableField<String>()
    val pesel = ObservableField<String>()
    val idCardNumber = ObservableField<String>()
    val idExpiryDate = ObservableField<String>()
    val paymentCardNumber = ObservableField<String>()
    val paymentCardExpiryDate = ObservableField<String>()
    val securityCode = ObservableField<String>()
    val email = ObservableField<String>()
    val phoneNumber = ObservableField<String>()
    val password1 = ObservableField<String>()
    val password2 = ObservableField<String>()
    val termsChecked = ObservableField<Boolean>()

    fun onRegister() {
        val validationSuccess = validateForm()
        if( validationSuccess ) {
            isLoading.set(true)
            userRepository.register(
                    name.get().orEmpty(),
                    surname.get().orEmpty(),
                    street.get().orEmpty(),
                    number.get().orEmpty(),
                    postalCode.get().orEmpty(),
                    city.get().orEmpty(),
                    pesel.get().orEmpty(),
                    idCardNumber.get().orEmpty(),
                    idExpiryDate.get().orEmpty(),
                    paymentCardNumber.get().orEmpty(),
                    paymentCardExpiryDate.get().orEmpty(),
                    securityCode.get().orEmpty(),
                    email.get().orEmpty(),
                    phoneNumber.get().orEmpty(),
                    password1.get().orEmpty(),
                    termsChecked.get() ?: false
            ).subscribe(
                    {
                        isLoading.set(false)
                        events.onNext(RegisterEvents.RegisterSuccess())
                    },
                    {
                        isLoading.set(false)
                        it.printStackTrace()
                        val error = errorHandler.getError(it)
                        events.onNext( RegisterEvents.RegisterFailure(error))
                    }
            )

        }
    }


    fun validateForm(): Boolean {
        return true
        //todo: implement validation
    }


    sealed class RegisterEvents {
        data class RegisterFailure(val error: ErrorHandler.Error): RegisterEvents()
        class RegisterSuccess: RegisterEvents()
    }


}