package com.spacitron.reposlistapp.reposlistviewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.utils.ItemSelectedListener
import com.spacitron.reposlistapp.utils.ItemShownListener
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException
import java.net.UnknownHostException

open class RepositoryViewModel : ViewModel(), ItemShownListener, ItemSelectedListener<Repository> {

    enum class DataError {
        NETWORK_ERROR, FORBIDDEN, OTHER
    }

    var repositoriesObservable: ObservableArrayList<Repository>? = null
    val isLoading = ObservableBoolean()
    val hasNext = ObservableBoolean()
    val itemSelected = ObservableField<Repository>()
    val error = ObservableField<DataError>()

    private val disposable = CompositeDisposable()
    private var repositoryProvider: CachedRepositoryManager? = null
    private var itemShownSubject: PublishSubject<Int>? = null


    fun initialise(repositoryProvider: CachedRepositoryManager) {
        if (repositoriesObservable == null) {
            repositoriesObservable = ObservableArrayList<Repository>()
            refresh(repositoryProvider)
        }
    }

    open fun refresh(repositoryProvider: CachedRepositoryManager) {

        repositoryProvider.setErrorListener{onError(it)}
        this.repositoryProvider = repositoryProvider


        val subscriptionFunction = {repoModels: List<Repository>? ->

            repoModels?.let {
                repositoriesObservable?.addAll(it)
            }

            hasNext.set(repositoryProvider.hasNext())
        }

        // This will keep track of the pages we requested so we only
        // make 1 request per scroll event
        itemShownSubject = PublishSubject.create()
        itemShownSubject
                ?.filter {
                    it >= (repositoriesObservable?.size ?: 0) - 3 && repositoryProvider.hasNext()
                }
                ?.map { (repositoriesObservable?.size ?: 0) - 3 }
                ?.distinct()
                ?.flatMapSingle { repositoryProvider.getNextRepos() }
                ?.subscribe(subscriptionFunction)
                ?.let { disposable.add(it) }


        isLoading.set(true)
        repositoryProvider.getNextRepos()
                .doOnEvent{_,_->
                    repositoriesObservable?.clear()
                    isLoading.set(false)
                }
                ?.subscribe(subscriptionFunction)
    }

    private fun onError(throwable: Throwable) {
        val errorOutput = when (throwable) {
            is UnknownHostException -> DataError.NETWORK_ERROR
            is HttpException -> if (throwable.code() == 403) {
                DataError.FORBIDDEN
            } else {
                DataError.NETWORK_ERROR
            }
            else -> DataError.OTHER
        }

        error.get()?.let {
            if (it == errorOutput) {
                // Force notification or you clients not receive new
                // errors unless a new exception is thrown
                error.notifyChange()
            }
        }
        error.set(errorOutput)
    }


    override fun itemWillBeShown(itemIndex: Int) {
        itemShownSubject?.onNext(itemIndex)
    }

    override fun itemSelected(item: Repository) {
        itemSelected.get()?.id.let {
            if (it == item.id) {
                // Force notification or you clients not receive new
                // errors unless a new exception is thrown
                itemSelected.notifyChange()
            }
        }
        itemSelected.set(item)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}