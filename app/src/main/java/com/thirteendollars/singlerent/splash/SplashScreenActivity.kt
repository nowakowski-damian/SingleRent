package com.thirteendollars.singlerent.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import com.thirteendollars.singlerent.R
import com.thirteendollars.singlerent.base.BaseActivity
import com.thirteendollars.singlerent.databinding.ActivitySplashScreenBinding
import com.thirteendollars.singlerent.injection.activity.ActivityComponent
import com.thirteendollars.singlerent.login.LoginActivity
import com.thirteendollars.singlerent.main.MainActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_splash_screen.*
import timber.log.Timber
import javax.inject.Inject

class SplashScreenActivity : BaseActivity<ActivitySplashScreenBinding>() {

    @Inject
    lateinit var splashScreenViewModel: SplashScreenViewModel

    override fun bindData(binding: ActivitySplashScreenBinding) {
        binding.viewModel = splashScreenViewModel
    }

    override fun subscribeViewModel(): CompositeDisposable? {
        val disposable = splashScreenViewModel
                .events
                .subscribe( this@SplashScreenActivity::handleEvents )
        return CompositeDisposable(disposable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splash_wheel.animation = AnimationUtils.loadAnimation(this, R.anim.spin)
        splash_wheel.animate()
        val permissions = getNotGrantedPermissions()
        if ( permissions.isEmpty() ) {
            startInitialization()
        }
        else {
            requestPermissions(permissions.toTypedArray(), REQUEST_CODE)
        }
    }

    private fun startInitialization() {
        splashScreenViewModel.checkCurrentUser()
    }


    private fun stopInitialization() {
        status_text.text = getString(R.string.permission_error)
        splash_wheel.animation.setAnimationListener( object : AnimationEndListener() {
            override fun onAnimationEnd(animation: Animation?) {
                this@SplashScreenActivity.finish()
            }
        })
        splash_wheel.animation.interpolator = DecelerateInterpolator()
        splash_wheel.animation.repeatCount = 0
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when( requestCode ) {
            Permissions.REQUEST_CODE -> {
                if( grantResults.find { it==PackageManager.PERMISSION_DENIED }==null ) {
                    startInitialization()
                }
                else {
                    stopInitialization()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    private fun handleEvents(event: SplashEvent) {
        when(event) {

            is SplashEvent.AuthenticationFailure -> {
                Timber.e(event.errorMessage)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            is SplashEvent.AuthenticationSuccess -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun provideLayout() = R.layout.activity_splash_screen

    private fun getNotGrantedPermissions(): MutableList<String> {
        val permissionList = mutableListOf<String>()

        if( !Permissions.isLocationGranted(this) ) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return permissionList
    }

    companion object Permissions {
        const val REQUEST_CODE = 1
        fun isLocationGranted(context: Context) =
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    abstract class AnimationEndListener: Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }
        override fun onAnimationStart(animation: Animation?) {
        }
    }
}
