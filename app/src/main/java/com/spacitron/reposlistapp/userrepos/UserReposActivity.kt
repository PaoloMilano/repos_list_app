package com.spacitron.reposlistapp.userrepos

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.spacitron.reposlistapp.R
import com.spacitron.reposlistapp.databinding.ActivityUserReposBinding
import com.spacitron.reposlistapp.githubservice.GitHubServiceProvider
import com.spacitron.reposlistapp.userrepos.userviewmodel.GitHubUserViewModel
import kotlinx.android.synthetic.main.activity_user_repos.*

class UserReposActivity : AppCompatActivity() {

    companion object {
        private val GITHUB_USER_NAME_KEY = "user_key"

        fun getParamIntent(callingActivity: Activity, githubUserName: String): Intent {
            val intent = Intent(callingActivity, UserReposActivity::class.java)
            return intent.putExtra(GITHUB_USER_NAME_KEY, githubUserName);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_repos)
        setSupportActionBar(toolbar)

        val userViewModel = ViewModelProviders.of(this).get(GitHubUserViewModel::class.java)

        val gitHubUserName = if (intent.hasExtra(GITHUB_USER_NAME_KEY)) {
            intent.getStringExtra(GITHUB_USER_NAME_KEY)
        } else {
            "JakeWharton"
        }

        val retroFitProvider = GitHubServiceProvider
        userViewModel.initialise(retroFitProvider, gitHubUserName)

        val binding: ActivityUserReposBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_repos)
        binding.userViewModel = userViewModel

        user_url_container.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(userViewModel.gitHubUser.get()?.htmlUrl)
            startActivity(i)
        }

        supportFragmentManager.beginTransaction().replace(list_container.id, UserRepoListFragment.newInstance(gitHubUserName)).commit()
    }
}
