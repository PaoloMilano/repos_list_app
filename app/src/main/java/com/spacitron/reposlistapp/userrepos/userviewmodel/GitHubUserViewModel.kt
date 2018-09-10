package com.spacitron.reposlistapp.userrepos.userviewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.spacitron.reposlistapp.githubservice.GitHubServiceProvider
import com.spacitron.reposlistapp.model.GitHubUser
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

open class GitHubUserViewModel : ViewModel() {

    val gitHubUser = ObservableField<GitHubUser>()

    private val disposable = CompositeDisposable()

    fun initialise(getUserFunction: (gitHubUserLogin: String) -> Single<GitHubUser?>, gitHubUserLogin: String) {
        getUserSingle(getUserFunction, gitHubUserLogin)
                .subscribe { gitUser ->
                    gitHubUser.set(gitUser)
                }
                .let { disposable.add(it) }
    }

    protected open fun getUserSingle(getUserFunction: (gitHubUserLogin: String) -> Single<GitHubUser?>, gitHubUserLogin: String): Single<GitHubUser?> = getUserFunction(gitHubUserLogin)
                .onErrorReturn {
                    // Get a user from cache or create a new one on error
                    val gitUser = getFromCache(gitHubUserLogin) ?: GitHubUser()

                    // If creating a new one set its name to the login string so we can display something to the user
                    if (gitUser.name == null) {
                        gitUser.name = gitHubUserLogin
                    }
                    gitUser
                }

    protected open fun getUserSingle(gitHubServiceProvider: GitHubServiceProvider, gitHubUserLogin: String): Single<GitHubUser?> {

        return gitHubServiceProvider
                .getUser(gitHubUserLogin)
                .onErrorReturn {
                    // Get a user from cache or create a new one on error
                    val gitUser = getFromCache(gitHubUserLogin) ?: GitHubUser()

                    // If creating a new one set its name to the login string so we can display something to the user
                    if (gitUser.name == null) {
                        gitUser.name = gitHubUserLogin
                    }
                    gitUser
                }
    }

    protected open fun getFromCache(gitHubUserLogin: String): GitHubUser? = GitHubUser().queryFirst { equalTo("login", gitHubUserLogin) }


    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}