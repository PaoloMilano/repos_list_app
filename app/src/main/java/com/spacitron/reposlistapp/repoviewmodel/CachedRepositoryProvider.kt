package com.spacitron.reposlistapp.repoviewmodel

import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.utils.ErrorListener
import com.vicpin.krealmextensions.querySorted
import com.vicpin.krealmextensions.saveAll
import io.reactivex.Maybe
import io.realm.Sort


class CachedRepositoryProvider(gitHubServiceProvider: GitHubServiceProvider, private val gitHubUser: String, private val itemsPerPage: Int = 15) {

    private var nextPage: Int
    private val gitHubService: GitHubService
    private var errorListener: ErrorListener? = null

    init {
        nextPage = 1
        gitHubService = gitHubServiceProvider.getGitHubService()
    }

    fun setErrorListener(errorListener: ErrorListener){
        this.errorListener = errorListener
    }

    fun hasNext() = nextPage != -1

    fun getNextReposMaybe(): Maybe<List<Repository>> {

        if (nextPage == -1) {
            return Maybe.never()
        }

        return gitHubService.getRepos(gitHubUser, nextPage, itemsPerPage)
                .onErrorReturn {
                    errorListener?.onError(it)
                    getFromCache()
                }
                .doOnEvent { repos, error ->
                    repos?.saveAll()
                    nextPage = if (repos != null || (repos?.size ?: 0 < itemsPerPage && error == null)) {
                        -1
                    } else {
                        nextPage + 1
                    }
                }
    }

    private fun getFromCache(): List<Repository> {

        val repos = Repository().querySorted("name", Sort.ASCENDING)

        val startIndex = if (repos.size > (nextPage - 1) * itemsPerPage) {
            (nextPage - 1) * itemsPerPage
        } else {
            repos.size
        }
        val endIndex = if (repos.size > startIndex + itemsPerPage) {
            startIndex + itemsPerPage
        } else {
            repos.size
        }

        nextPage = if (endIndex - startIndex < itemsPerPage) {
            -1
        } else {
            nextPage + 1
        }

        if (startIndex < 0 || endIndex < 0) {
            return ArrayList()
        }

        return repos.subList(startIndex, endIndex)
    }
}