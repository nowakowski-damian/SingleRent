package com.thirteendollars.singlerent.main.account

import android.databinding.ObservableField
import com.thirteendollars.singlerent.data.repository.UserRepository
import com.thirteendollars.singlerent.handler.ErrorHandler
import com.thirteendollars.singlerent.injection.application.FragmentScope
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Damian Nowakowski on 13/04/2018.
 * mail: thirteendollars.com@gmail.com
 */

@FragmentScope
class AccountViewModel @Inject constructor(
        val userRepository: UserRepository,
        val errorHandler: ErrorHandler
) {

    val events = PublishSubject.create<AccountViewModel.AccountEvents>()

    val isLoading =  PublishSubject.create<Boolean>()

    val isEditableMode = ObservableField<Boolean>(false)
    val name = ObservableField<String>("")
    val surname = ObservableField<String>("")
    val street = ObservableField<String>("")
    val number = ObservableField<String>("")
    val postalCode = ObservableField<String>("")
    val city = ObservableField<String>("")
    val paymentCardNumber = ObservableField<String>("")
    val paymentCardExpiryDate = ObservableField<String>("")
    val securityCode = ObservableField<String>("")
    val phoneNumber = ObservableField<String>("")
    val password1 = ObservableField<String>("")
    val password2 = ObservableField<String>("")

    fun fetchData() {
        isLoading.onNext(true)
        userRepository.getCurrentUser().subscribe(
            {
                it.user.let {
                    name.set(it.name)
                    surname.set(it.surname)
                    street.set(it.street)
                    number.set(it.number)
                    postalCode.set(it.postalCode)
                    city.set(it.city)
                    paymentCardNumber.set(it.paymentCardNumber)
                    paymentCardExpiryDate.set(it.paymentCardExpiryDate)
                    securityCode.set(it.securityCode)
                    phoneNumber.set(it.phoneNumber)
                    password1.set(it.password)
                    password2.set(it.password)
                }
                isLoading.onNext(false)
            },
            {
                it.printStackTrace()
                isLoading.onNext(false)
                val error = errorHandler.getError(it)
                val event = AccountEvents.HttpError(error)
                events.onNext(event)
            }
        )
    }

    fun onLogout() {
        isLoading.onNext(true)
        userRepository.logout().subscribe(
            {
                isLoading.onNext(false)
                events.onNext(AccountEvents.LogoutSuccess())
            },
            {
                it.printStackTrace()
                isLoading.onNext(false)
                val error = errorHandler.getError(it)
                val event = AccountEvents.HttpError(error)
                events.onNext(event)
            }
        )
    }

    fun onButton() {
        if( isEditableMode.get()==true ) {
            saveChanges()
        }
        else {
            isEditableMode.set(true)
        }
    }

    private fun saveChanges() {
        val validationSuccess = validateForm()
        if(validationSuccess) {
            isLoading.onNext(true)
            userRepository.changeUserData(
                    street.get()!!,
                    number.get()!!,
                    postalCode.get()!!,
                    city.get()!!,
                    phoneNumber.get()!!,
                    paymentCardNumber.get(),
                    paymentCardExpiryDate.get(),
                    securityCode.get(),
                    password1.get()
            ).subscribe(
                    {
                        isEditableMode.set(false)
                        isLoading.onNext(false)
                        events.onNext(AccountEvents.DataChangeSuccess())
                    },
                    {
                        it.printStackTrace()
                        isLoading.onNext(false)
                        val error = errorHandler.getError(it)
                        val event = AccountEvents.HttpError(error)
                        events.onNext(event)
                    }
            )
        }


    }

    fun validateForm(): Boolean {
        return true
        //todo: implement validation
    }



    sealed class AccountEvents {
        data class HttpError(val error:ErrorHandler.Error): AccountEvents()
        class LogoutSuccess: AccountEvents()
        class DataChangeSuccess: AccountEvents()
    }
}