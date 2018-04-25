package com.thirteendollars.singlerent.login

import android.content.Intent
import com.thirteendollars.singlerent.R
import com.thirteendollars.singlerent.base.BaseActivity
import com.thirteendollars.singlerent.databinding.ActivityLoginBinding
import com.thirteendollars.singlerent.injection.activity.ActivityComponent
import com.thirteendollars.singlerent.main.MainActivity
import com.thirteendollars.singlerent.register.RegisterActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    @Inject
    lateinit var loginViewModel: LoginViewModel


    override fun subscribeViewModel(): CompositeDisposable? {
        val disposable = loginViewModel
                .events
                .subscribe( this@LoginActivity::handleEvents )
        return CompositeDisposable(disposable)
    }

    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun provideLayout() = R.layout.activity_login

    override fun bindData(binding: ActivityLoginBinding) {
        binding.viewModel = loginViewModel
    }

    private fun handleEvents(event: LoginEvent) {
        when(event) {

            is LoginEvent.ValidationFailure -> {
                loginButton.startLoading()
                loginButton.loadingFailed()
                showToast(getString(R.string.empty_credentials_error))
            }

            is LoginEvent.LoginStarted -> {
                loginButton.startLoading()
            }

            is LoginEvent.LoginSuccess -> {
                loginButton.loadingSuccessful()
                val intent = Intent(this, MainActivity::class.java)
//                event.currentDisposition?.apply {
//                    val bundle = Bundle()
//                    bundle.putSerializable(MainActivity.Param.DISPOSITION, this)
//                    intent.putExtras(bundle)
//                }
                startActivity(intent)
                finish()
            }

            is LoginEvent.LoginFailure -> {
                loginButton.loadingFailed()
                showToast( event.errorMessage )
            }

            is LoginEvent.RegisterScreen -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }

    }
}
