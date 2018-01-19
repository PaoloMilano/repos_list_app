package com.spacitron.reposlistapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.spacitron.reposlistapp.databinding.ActivityMainBinding
import com.spacitron.reposlistapp.reposervice.serviceproviders.RetrofitGitHubServiceProvider
import com.spacitron.reposlistapp.repoviewmodel.CachedRepositoryProvider
import com.spacitron.reposlistapp.repoviewmodel.RepositoryViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repositoryViewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)
        initialiseViewModel(repositoryViewModel)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.repoViewModel = repositoryViewModel
        binding.onItemSelectedListener = repositoryViewModel
        binding.onItemShownListener = repositoryViewModel

        val observableRepositorySelection = repositoryViewModel.itemSelected
        observableRepositorySelection.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {

                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(observableRepositorySelection.get().htmlUrl)
                        startActivity(i)

                    }
                })

        val observableError = repositoryViewModel.error
        observableError.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {

                        val errorString = when (observableError.get()) {
                            RepositoryViewModel.DataError.FORBIDDEN -> getString(R.string.error_forbidden)
                            RepositoryViewModel.DataError.NETWORK_ERROR -> getString(R.string.error_network)
                            RepositoryViewModel.DataError.OTHER -> getString(R.string.error_other)
                        }

                        Snackbar.make(root_view, errorString, Snackbar.LENGTH_SHORT).show()
                    }
                })
        pull_to_refresh.setOnRefreshListener {
            pull_to_refresh.isRefreshing = false
            initialiseViewModel(repositoryViewModel)
        }

    }


    private fun initialiseViewModel(repositoryViewModel: RepositoryViewModel){
        repositoryViewModel.initialise(CachedRepositoryProvider(RetrofitGitHubServiceProvider(), "JakeWharton"))
    }
}
