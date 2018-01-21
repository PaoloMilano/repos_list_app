package com.spacitron.reposlistapp.userviewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.vicpin.krealmextensions.queryFirst
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class GitHubUserViewModel : ViewModel() {

    val gitHubUser = ObservableField<GitHubUser>()

    private val disposable = CompositeDisposable()


    fun initialise(gitHubServiceProvider: GitHubServiceProvider, gitHubUserLogin: String) {

        gitHubServiceProvider.
                getGitHubService()
                .getUser(gitHubUserLogin)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { gitUser, error ->
                    if (gitUser == null) {
                        val user = GitHubUser().queryFirst { equalTo("login", gitHubUserLogin) }
                        if (user?.name == null) {
                            user?.name = gitHubUserLogin
                        }
                    } else {
                        gitHubUser.set(gitUser)
                    }
                }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}