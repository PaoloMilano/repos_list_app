package com.spacitron.reposlistapp.reposlistviewmodel

import com.spacitron.reposlistapp.model.Repository
import com.vicpin.krealmextensions.deleteAll
import com.vicpin.krealmextensions.querySorted
import com.vicpin.krealmextensions.saveAll
import io.reactivex.Single
import io.realm.Sort


open class CachedRepositoryManager(private val repoListFetcher: (page:Int, perPage:Int)-> Single<List<Repository>?>, private val itemsPerPage: Int = 15) {

    private var nextPage: Int
    private var errorListener: ((t: Throwable) -> Unit)? = null

    init {
        nextPage = 1
    }

    fun setErrorListener(errorListener: (t: Throwable) -> Unit) {
        this.errorListener = errorListener
    }

    open fun hasNext() = nextPage != -1

    open fun getNextRepos(): Single<List<Repository>?> {

        if (nextPage == -1) {
            return Single.never()
        }

        return repoListFetcher(nextPage, itemsPerPage)
                .doOnEvent { repos, _ ->
                    repos?.let {
                        saveReposToCache(it)
                    }
                }
                .onErrorReturn {
                    errorListener?.invoke(it)
                    getFromCache()
                }
                .doOnEvent { repos, _ ->
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
        if (nextPage == 1) {
            Repository().deleteAll()
        }
        repos?.saveAll()
    }


    protected open fun getFromCache(): List<Repository> {

        if (!hasNext()) {
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