package com.spacitron.reposlistapp.repoviewmodel

import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import io.reactivex.Maybe


class RepositoryProvider(gitHubServiceProvider: GitHubServiceProvider, private val gitHubUser: String, private val itemsPerPage: Int = 15) {

    private var nextPage = 1

    private val gitHubService: GitHubService

    init {
        gitHubService = gitHubServiceProvider.getGitHubService()
    }

    fun hasNext() = nextPage != -1


    fun getNextReposMaybe(): Maybe<List<Repository>> {

        if(nextPage == -1){
            return Maybe.never()
        }

        return gitHubService.getRepos(gitHubUser, nextPage, itemsPerPage)
                .doOnEvent { repos, error ->

                    nextPage = if(repos.size < itemsPerPage && error == null){
                         -1
                    }else{
                        nextPage +1
                    }
                }
    }
}