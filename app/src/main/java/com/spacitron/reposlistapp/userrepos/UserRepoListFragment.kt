package com.spacitron.reposlistapp.userrepos


import android.os.Bundle
import com.spacitron.reposlistapp.githubservice.GitHubServiceProvider
import com.spacitron.reposlistapp.reposlistfragment.RepoListFragment

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

    override fun fetchRepos() = {page: Int, perPage: Int ->  arguments?.getString(GITHUB_LOGIN_KEY).let { GitHubServiceProvider.getRepos("", page,perPage)} }
}