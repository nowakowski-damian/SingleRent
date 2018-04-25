package com.thirteendollars.singlerent.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import android.view.View
import com.thirteendollars.singlerent.R
import com.thirteendollars.singlerent.base.BaseNfcActivity
import com.thirteendollars.singlerent.databinding.ActivityMainBinding
import com.thirteendollars.singlerent.injection.activity.ActivityComponent
import com.thirteendollars.singlerent.login.LoginActivity
import com.thirteendollars.singlerent.main.account.AccountFragment
import com.thirteendollars.singlerent.main.map.MapFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseNfcActivity<ActivityMainBinding>(), BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottom_navigation.setOnNavigationItemSelectedListener(this)
        fragmentManager.beginTransaction().add(R.id.main_container, MapFragment() ).commit()
    }

    override fun inject(component: ActivityComponent) {
        component.inject(this)
    }

    override fun provideLayout(): Int = R.layout.activity_main

    override fun bindData(binding: ActivityMainBinding) {
        binding.viewModel = mainViewModel
    }

    override fun subscribeViewModel(): CompositeDisposable? {
        val eventDisposable = mainViewModel
                .events
                .subscribe(this@MainActivity::handleEvents)
        return CompositeDisposable(eventDisposable)
    }

    private fun handleEvents(event: MainEvent) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_map -> fragmentManager.beginTransaction().replace(R.id.main_container, MapFragment() ).commit()
            R.id.action_account -> fragmentManager.beginTransaction().replace(R.id.main_container, AccountFragment() ).commit()
        }
        return true
    }

    fun showLoadingOverlay(show: Boolean) {
        binding.loadingOverlay.visibility = if(show) View.VISIBLE else View.GONE
    }

    fun showLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onNfcUnavailable() {
        showToast(getString(R.string.nfc_off_info))
    }
}
