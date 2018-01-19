package com.spacitron.reposlistapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.spacitron.reposlistapp.databinding.ActivityMainBinding
import com.spacitron.reposlistapp.repoviewmodel.RepositoryProvider
import com.spacitron.reposlistapp.reposervice.serviceproviders.RetrofitGitHubServiceProvider
import com.spacitron.reposlistapp.repoviewmodel.RepositoryViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repositoryViewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.repoViewModel = repositoryViewModel

        repositoryViewModel.initialise(RepositoryProvider(RetrofitGitHubServiceProvider(), "JakeWharton"))

        val observableCitySelection = repositoryViewModel.itemSelected
        observableCitySelection.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {

                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(observableCitySelection.get().html_url)
                        startActivity(i)

                    }
                })
    }
}
