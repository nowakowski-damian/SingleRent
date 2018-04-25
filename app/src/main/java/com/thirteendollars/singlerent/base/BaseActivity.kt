package com.thirteendollars.singlerent.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.thirteendollars.singlerent.App
import com.thirteendollars.singlerent.injection.activity.ActivityComponent
import com.thirteendollars.singlerent.injection.activity.ActivityModule
import com.thirteendollars.singlerent.injection.activity.DaggerActivityComponent
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Damian Nowakowski on 18/03/2018.
 * mail: thirteendollars.com@gmail.com
 */
abstract class BaseActivity<BINDING:ViewDataBinding>: AppCompatActivity() {

    protected lateinit var binding: BINDING
    protected var subscription: CompositeDisposable? = null

    val component: ActivityComponent by lazy {
        DaggerActivityComponent
                .builder()
                .activityModule(ActivityModule(this))
                .appComponent( (application as App).component )
                .build()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(component)
        initDataBinding()
        subscription = subscribeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.clear()
    }

    abstract fun inject(component: ActivityComponent)
    @LayoutRes abstract fun provideLayout(): Int
    abstract fun bindData(binding: BINDING)
    abstract fun subscribeViewModel(): CompositeDisposable?


    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, provideLayout())
        bindData(binding)
        binding.executePendingBindings()
    }

    fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

}