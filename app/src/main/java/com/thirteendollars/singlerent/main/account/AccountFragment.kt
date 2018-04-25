package com.thirteendollars.singlerent.main.account


import com.thirteendollars.singlerent.R
import com.thirteendollars.singlerent.base.BaseFragment
import com.thirteendollars.singlerent.databinding.FragmentAccountBinding
import com.thirteendollars.singlerent.injection.application.FragmentComponent
import com.thirteendollars.singlerent.main.MainActivity
import com.thirteendollars.singlerent.main.account.AccountViewModel.AccountEvents
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class AccountFragment : BaseFragment<FragmentAccountBinding>() {

    @Inject
    lateinit var viewModel: AccountViewModel

    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchData()
    }

    override fun provideLayout(): Int = R.layout.fragment_account

    override fun bindData(binding: FragmentAccountBinding) {
        binding.accountViewModel = viewModel
    }

    override fun subscribeViewModel(): CompositeDisposable? {
        val events = viewModel.events.subscribe(this@AccountFragment::handleEvents)
        val loading = viewModel.isLoading.subscribe((activity as MainActivity)::showLoadingOverlay)
        return CompositeDisposable(events,loading)
    }

    private fun handleEvents(event: AccountEvents) {
        when(event) {
            is AccountEvents.HttpError -> {
                showToast(event.error.message)
                if(event.error.is401) (activity as MainActivity).showLoginActivity()
            }
            is AccountEvents.LogoutSuccess -> {
                (activity as MainActivity).showLoginActivity()
            }
            is AccountEvents.DataChangeSuccess -> {
                showToast(getString(R.string.account_save_data_success))
            }
        }
    }

}
