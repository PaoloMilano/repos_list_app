package com.spacitron.reposlistapp.githubservice

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.model.RepositoryResponse
import io.reactivex.Single

object GitHubServiceProvider {

    private var githubService: GitHubService? = null
    private var githubWebService: GitHubWebService? = null

    fun initialise(githubService: GitHubService?, githubWebService: GitHubWebService?) {
        this.githubService = githubService
        this.githubWebService = githubWebService

    }

    fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {
        return githubService!!.getRepos(user, page, perPage)
    }

    fun recentRepos(page: Int, perPage: Int): Single<RepositoryResponse?> {
        return githubService!!.recentRepos(page, perPage)
    }

    fun getUser(user: String): Single<GitHubUser?> {
        return githubService!!.getUser(user)
    }

    fun getTrendingRepos(): Single<List<Repository>?> {
        return githubWebService!!.getTrendingRepos().map { GitHubWebRepoDeserialiser.map(String(it.bytes())) }
    }
}