package com.thirteendollars.singlerent.register

import com.thirteendollars.singlerent.R
import com.thirteendollars.singlerent.base.BaseActivity
import com.thirteendollars.singlerent.databinding.ActivityRegisterBinding
import com.thirteendollars.singlerent.injection.activity.ActivityComponent
import com.thirteendollars.singlerent.register.RegisterViewModel.RegisterEvents
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {

    @Inject lateinit var registerViewModel: RegisterViewModel


    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun provideLayout(): Int = R.layout.activity_register

    override fun bindData(binding: ActivityRegisterBinding) {
        binding.registerViewModel = registerViewModel
    }

    override fun subscribeViewModel(): CompositeDisposable? {
        return CompositeDisposable(registerViewModel.events.subscribe(this@RegisterActivity::handleEvents))
    }

    private fun handleEvents(event: RegisterEvents) {
        when(event) {
            is RegisterEvents.RegisterSuccess -> {
                showToast(getString(R.string.registration_success))
                finish()
            }
            is RegisterEvents.RegisterFailure -> {
                showToast(event.error.message.orEmpty())
            }
        }
    }
}
