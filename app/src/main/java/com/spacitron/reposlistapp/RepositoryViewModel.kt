package com.spacitron.reposlistapp

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RepositoryViewModel : ViewModel(), ItemSelectedListener<Repository> {


    val repositoriesObservable = ObservableArrayList<Repository>()
    val isLoading = ObservableBoolean()
    val itemSelected = ObservableField<Repository>()


    fun initialise(repositoryProvider: RepositoryProvider) {

        isLoading.set(true)

        repositoryProvider.getNextReposMaybe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isLoading.set(false)
                    repositoriesObservable.addAll(it)
                }
    }

    override fun itemSelected(item: Repository) {
        itemSelected.set(item)
    }
}