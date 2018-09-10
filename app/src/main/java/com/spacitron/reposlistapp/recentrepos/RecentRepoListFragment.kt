package com.spacitron.reposlistapp.recentrepos


import com.spacitron.reposlistapp.abstractreposlistfragment.RepoListFragment
import com.spacitron.reposlistapp.githubservice.GitHubServiceProvider

class RecentRepoListFragment : RepoListFragment() {

    companion object {
        fun newInstance(): RecentRepoListFragment {
            return RecentRepoListFragment()
        }
    }

//    override fun fetchRepos() = { page: Int, perPage: Int -> GitHubServiceProvider.getTrendingRepos() }
    override fun fetchRepos() = { page: Int, perPage: Int -> GitHubServiceProvider.recentRepos(1, 1).map { it.items } }
}