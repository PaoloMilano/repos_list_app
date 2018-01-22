package com.spacitron.reposlistapp


import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spacitron.reposlistapp.databinding.FragmentRepoListBinding
import com.spacitron.reposlistapp.reposervice.serviceproviders.RetrofitGitHubServiceProvider
import com.spacitron.reposlistapp.repoviewmodel.CachedRepositoryManager
import com.spacitron.reposlistapp.repoviewmodel.RepositoryViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_repo_list.view.*

class RepoListFragment : Fragment() {


    companion object {

        private val GITHUB_LOGIN_KEY = "user_login_key"

        fun getInstance(gitHubUserLogin: String): RepoListFragment {
            val bundle = Bundle()
            bundle.putString(GITHUB_LOGIN_KEY, gitHubUserLogin)

            val repoListFragment = RepoListFragment()
            repoListFragment.arguments = bundle
            return repoListFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val repositoryViewModel = ViewModelProviders.of(activity as FragmentActivity).get(RepositoryViewModel::class.java)


        val gitHubUserLogin = arguments?.getString(GITHUB_LOGIN_KEY)
        gitHubUserLogin?.let {
            repositoryViewModel.initialise(CachedRepositoryManager(RetrofitGitHubServiceProvider(), it))
        }


        val observableRepositorySelection = repositoryViewModel.itemSelected
        observableRepositorySelection.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(observableRepositorySelection.get().htmlUrl)
                        activity?.startActivity(i)
                    }
                })


        val repoListBinding: FragmentRepoListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_repo_list, container, false)

        repoListBinding.repoViewModel = repositoryViewModel
        repoListBinding.onItemSelectedListener = repositoryViewModel
        repoListBinding.onItemShownListener = repositoryViewModel

        repoListBinding.getRoot().pull_to_refresh.setOnRefreshListener {
            gitHubUserLogin?.let {
                repositoryViewModel.refresh(CachedRepositoryManager(RetrofitGitHubServiceProvider(), it))
            }
        }

        val observableError = repositoryViewModel.error
        observableError.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {

                        val errorString = when (observableError.get()) {
                            RepositoryViewModel.DataError.FORBIDDEN -> getString(R.string.error_forbidden)
                            RepositoryViewModel.DataError.NETWORK_ERROR -> getString(R.string.error_network)
                            RepositoryViewModel.DataError.OTHER -> getString(R.string.error_other)
                        }

                        Snackbar.make(root_view, errorString, Snackbar.LENGTH_SHORT).show()
                    }
                })

        repoListBinding.executePendingBindings()
        return repoListBinding.getRoot()
    }
}