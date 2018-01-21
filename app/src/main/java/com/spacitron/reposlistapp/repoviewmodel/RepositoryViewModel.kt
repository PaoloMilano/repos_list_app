package com.spacitron.reposlistapp.repoviewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.spacitron.reposlistapp.utils.ErrorListener
import com.spacitron.reposlistapp.utils.ItemSelectedListener
import com.spacitron.reposlistapp.utils.ItemShownListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException
import java.net.UnknownHostException

open class RepositoryViewModel : ViewModel(), ItemShownListener, ItemSelectedListener<RepositoryModel>, ErrorListener {

    enum class DataError {
        NETWORK_ERROR, FORBIDDEN, OTHER
    }

    var repositoriesObservable: ObservableArrayList<RepositoryDisplayModel>? = null
    val isLoading = ObservableBoolean()
    val itemSelected = ObservableField<RepositoryModel>()
    val error = ObservableField<DataError>()

    private val disposable = CompositeDisposable()
    private var repositoryProvider: CachedRepositoryManager? = null
    private var itemShownSubject: PublishSubject<Int>? = null


    fun initialise(repositoryProvider: CachedRepositoryManager) {
        if(repositoriesObservable == null){
            repositoriesObservable = ObservableArrayList<RepositoryDisplayModel>()
            refresh(repositoryProvider)
        }
    }

    open fun refresh(repositoryProvider: CachedRepositoryManager) {
        repositoryProvider.setErrorListener(this)
        this.repositoryProvider = repositoryProvider

        repositoriesObservable?.clear()

        // This will keep track of the pages we requested so we only
        // make 1 request per scroll event
        itemShownSubject = PublishSubject.create()
        itemShownSubject
                ?.filter {
                    it == (repositoriesObservable?.size ?:0) - 4 && repositoryProvider?.hasNext()
                }
                ?.distinct()
                ?.subscribe {
                    getNextRepositories()
                }
                ?.let { disposable.add(it) }

        isLoading.set(true)
        getNextRepositories()
    }

    protected open fun getNextRepositories() {
        repositoryProvider?.getNextRepos()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.map {
                    // Map data model to display classes
                    it.map { RepositoryModel(it) }
                }
                ?.subscribe { repoModels, error ->

                    isLoading.set(false)
                    repositoriesObservable?.addAll(repoModels)

                    repositoriesObservable?.remove(PlaceholderRepositoryModel)
                    if (repositoryProvider?.hasNext() ?: false) {
                        repositoriesObservable?.add(PlaceholderRepositoryModel)
                    }
                }
                ?.let { disposable.add(it) }
    }

    override fun onError(throwable: Throwable) {
        val errorOutput = when (throwable) {
            is UnknownHostException -> DataError.NETWORK_ERROR
            is HttpException -> if (throwable.code() == 403) {
                DataError.FORBIDDEN
            } else {
                DataError.NETWORK_ERROR
            }
            else -> DataError.OTHER
        }

        error.set(errorOutput)
        // Force notification or you clients not receive new
        // errors unless a new exception is thrown
        error.notifyChange()
    }


    override fun itemWillBeShown(itemIndex: Int) {
        itemShownSubject?.onNext(itemIndex)
    }

    override fun itemSelected(item: RepositoryModel) {
        itemSelected.set(item)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}