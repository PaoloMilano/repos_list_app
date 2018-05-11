package com.spacitron.reposlistapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.spacitron.reposlistapp.databinding.ActivityMainBinding
import com.spacitron.reposlistapp.reposervice.serviceproviders.RetrofitGitHubServiceProvider
import com.spacitron.reposlistapp.userviewmodel.GitHubUserViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val GITHUB_USER_NAME = "JakeWharton"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val userViewModel = ViewModelProviders.of(this).get(GitHubUserViewModel::class.java)

        val retroFitProvider = RetrofitGitHubServiceProvider
        userViewModel.initialise(retroFitProvider, GITHUB_USER_NAME)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.userViewModel = userViewModel

        user_url_container.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(userViewModel.gitHubUser.get()?.htmlUrl)
            startActivity(i)
        }

        supportFragmentManager.beginTransaction().replace(list_container.id, RepoListFragment.getInstance(GITHUB_USER_NAME)).commit()
    }
}
