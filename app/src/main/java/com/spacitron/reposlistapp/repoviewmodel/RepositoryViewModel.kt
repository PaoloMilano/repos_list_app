package com.spacitron.reposlistapp.repoviewmodel

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.spacitron.reposlistapp.utils.ErrorListener
import com.spacitron.reposlistapp.utils.ItemSelectedListener
import com.spacitron.reposlistapp.utils.ItemShownListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException

class RepositoryViewModel : ViewModel(), ItemShownListener, ItemSelectedListener<RepositoryModel>, ErrorListener {

    enum class DataError {
        NETWORK_ERROR, FORBIDDEN, OTHER
    }

    val repositoriesObservable = ObservableArrayList<RepositoryDisplayModel>()
    val isLoading = ObservableBoolean()
    val itemSelected = ObservableField<RepositoryModel>()
    val error = ObservableField<DataError>()

    private var repositoryProvider: CachedRepositoryProvider? = null


    fun initialise(repositoryProvider: CachedRepositoryProvider) {
        repositoryProvider.setErrorListener(this)
        isLoading.set(true)
        this.repositoryProvider = repositoryProvider
        repositoriesObservable.clear()
        getNextRepositories()
    }

    private fun getNextRepositories() {
        repositoryProvider?.getNextReposMaybe()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.map {
                    // Map data model to display classes
                    it.map { RepositoryModel(it) }
                }
                ?.subscribe {
                    isLoading.set(false)
                    repositoriesObservable.addAll(it)

                    repositoriesObservable.remove(PlaceholderRepositoryModel)
                    if (repositoryProvider?.hasNext() ?: false) {
                        repositoriesObservable.add(PlaceholderRepositoryModel)
                    }
                }

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
        if (itemIndex == repositoriesObservable.size - 3 && repositoryProvider?.hasNext() ?: false) {
            getNextRepositories()
        }
    }

    override fun itemSelected(item: RepositoryModel) {
        itemSelected.set(item)
    }

}