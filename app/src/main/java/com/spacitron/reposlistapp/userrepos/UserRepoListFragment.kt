package com.spacitron.reposlistapp.userrepos


import android.os.Bundle
import com.spacitron.reposlistapp.githubservice.GitHubServiceProvider
import com.spacitron.reposlistapp.abstractreposlistfragment.RepoListFragment
import io.reactivex.Single

class UserRepoListFragment : RepoListFragment() {

    companion object {

        private val GITHUB_LOGIN_KEY = "user_login_key"

        fun newInstance(gitHubUserLogin: String): UserRepoListFragment {
            val bundle = Bundle()
            bundle.putString(GITHUB_LOGIN_KEY, gitHubUserLogin)

            val repoListFragment = UserRepoListFragment()
            repoListFragment.arguments = bundle
            return repoListFragment
        }
    }

    override fun fetchRepos() = { page: Int, perPage: Int ->
        val arguments = arguments
        if (arguments != null) {
            arguments.getString(GITHUB_LOGIN_KEY).let { GitHubServiceProvider.getRepos(it, page, perPage) }
        }else {
           Single.never()
        }
    }

}