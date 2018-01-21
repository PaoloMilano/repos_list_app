package com.spacitron.reposlistapp.repoviewmodel

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.utils.ErrorListener
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.Single


open class GitHubUserProvider(gitHubServiceProvider: GitHubServiceProvider, private val gitHubUser: String) {

    private val gitHubService: GitHubService
    private var errorListener: ErrorListener? = null

    init {
        gitHubService = gitHubServiceProvider.getGitHubService()
    }

    fun setErrorListener(errorListener: ErrorListener) {
        this.errorListener = errorListener
    }


    fun getUser(): Single<GitHubUser> {
        return gitHubService.getUser(gitHubUser)
                .onErrorReturn {
                    errorListener?.onError(it)
                    GitHubUser().queryFirst{ equalTo("login", gitHubUser)}
                }
    }
}