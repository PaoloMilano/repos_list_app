package com.spacitron.reposlistapp.abstractreposlistfragment


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spacitron.reposlistapp.R
import com.spacitron.reposlistapp.databinding.FragmentRepoListBinding
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposlistviewmodel.CachedRepositoryManager
import com.spacitron.reposlistapp.reposlistviewmodel.RepositoryViewModel
import com.spacitron.reposlistapp.userrepos.ReposRecyclerViewAdapter
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_repo_list.view.*

abstract class RepoListFragment : Fragment() {

    protected abstract fun fetchRepos(): (Int, Int) -> Single<List<Repository>?>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val repositoryViewModel = ViewModelProviders.of(activity as FragmentActivity).get(RepositoryViewModel::class.java)

        repositoryViewModel.initialise(CachedRepositoryManager(fetchRepos()))

        val observableRepositorySelection = repositoryViewModel.itemSelected
        observableRepositorySelection.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(observableRepositorySelection.get()?.htmlUrl)
                        activity?.startActivity(i)
                    }
                })


        val repoListBinding: FragmentRepoListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_repo_list, container, false)

        repoListBinding.repoViewModel = repositoryViewModel
        repoListBinding.onItemSelectedListener = repositoryViewModel
        repoListBinding.onItemShownListener = repositoryViewModel

        repoListBinding.getRoot().recycler_view.layoutManager = LinearLayoutManager(context)
        repoListBinding.getRoot().recycler_view.adapter = ReposRecyclerViewAdapter()

        repoListBinding.getRoot().pull_to_refresh.setOnRefreshListener {
            repositoryViewModel.refresh(CachedRepositoryManager(fetchRepos()))
        }

        val observableError = repositoryViewModel.error
        observableError.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {

                        val errorString = when (observableError.get()) {
                            RepositoryViewModel.DataError.FORBIDDEN -> getString(R.string.error_forbidden)
                            RepositoryViewModel.DataError.NETWORK_ERROR -> getString(R.string.error_network)
                            else -> {
                                getString(R.string.error_other)
                            }
                        }

                        Snackbar.make(repoListBinding.root, errorString, Snackbar.LENGTH_SHORT).show()
                    }
                })

        repoListBinding.executePendingBindings()
        return repoListBinding.getRoot()
    }
}