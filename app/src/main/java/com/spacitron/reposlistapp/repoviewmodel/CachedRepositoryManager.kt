package com.spacitron.reposlistapp.repoviewmodel

import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.utils.ErrorListener
import com.vicpin.krealmextensions.deleteAll
import com.vicpin.krealmextensions.querySorted
import com.vicpin.krealmextensions.saveAll
import io.reactivex.Single
import io.realm.Sort


open class CachedRepositoryManager(gitHubServiceProvider: GitHubServiceProvider, private val gitHubUser: String, private val itemsPerPage: Int = 15) {

    private var nextPage: Int
    private val gitHubService: GitHubService
    private var errorListener: ErrorListener? = null

    init {
        nextPage = 1
        gitHubService = gitHubServiceProvider.getGitHubService()
    }

    fun setErrorListener(errorListener: ErrorListener) {
        this.errorListener = errorListener
    }

    open fun hasNext() = nextPage != -1

    fun getNextRepos(): Single<List<Repository>> {

        if (nextPage == -1) {
            return Single.never()
        }

        return gitHubService.getRepos(gitHubUser, nextPage, itemsPerPage)
                .doOnEvent { repos, error ->
                    saveReposToCache(repos)
                }
                .onErrorReturn {
                    errorListener?.onError(it)
                    getFromCache()
                }
                .doOnEvent{ repos, error ->
                    nextPage = if (repos != null && repos?.size < itemsPerPage) {
                        -1
                    } else {
                        nextPage + 1
                    }
                }

    }

    protected open fun saveReposToCache(repos: List<Repository>) {

        // To keep the cache clean and up to date just delete all items
        // when we can successfully start fetching the list from the first page
        if (nextPage == 1 && repos != null) {
            Repository().deleteAll()
        }
        repos?.saveAll()
    }


    protected open fun getFromCache(): List<Repository> {

        if(!hasNext()){
            return emptyList()
        }

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

        return repos.subList(startIndex, endIndex)
    }
}