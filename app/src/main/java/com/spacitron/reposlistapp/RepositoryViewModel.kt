package com.spacitron.reposlistapp

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RepositoryViewModel: ViewModel() {

    val repositoriesObservable = ObservableArrayList<Repository>()

    fun initialise(repositoryProvider: RepositoryProvider) {
        repositoryProvider.getNextReposMaybe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    repositoriesObservable.addAll(it)
                }
    }
}