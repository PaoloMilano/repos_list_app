package com.spacitron.reposlistapp.userviewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class GitHubUserViewModel : ViewModel() {

    val gitHubUser = ObservableField<GitHubUser>()

    private val disposable = CompositeDisposable()


    fun initialise(gitHubServiceProvider: GitHubServiceProvider, gitHubUserLogin: String) {
        getUserSingle(gitHubServiceProvider, gitHubUserLogin)
                .subscribe { gitUser ->
                    gitHubUser.set(gitUser)
                }
                .let { disposable.add(it) }
    }

    protected open fun getUserSingle(gitHubServiceProvider: GitHubServiceProvider, gitHubUserLogin: String): Single<GitHubUser?> {

        return gitHubServiceProvider.
                getGitHubService()
                .getUser(gitHubUserLogin)
                .onErrorReturn {
                    // Get a user from cache or create a new one on error
                    val gitUser = getFromCache(gitHubUserLogin) ?: GitHubUser()

                    // If creating a new one set its name to the login string so we can display something to the user
                    if (gitUser?.name == null) {
                        gitUser?.name = gitHubUserLogin
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