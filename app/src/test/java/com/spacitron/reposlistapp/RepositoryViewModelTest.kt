package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.repoviewmodel.CachedRepositoryProvider
import com.spacitron.reposlistapp.repoviewmodel.RepositoryDisplayModel
import com.spacitron.reposlistapp.repoviewmodel.RepositoryViewModel
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Test

class RepositoryViewModelTest {


    @Test
    fun testPageOnlyCalledOnce() {

        val repositoryViewModel = TestyRepositoryViewModel()
        repositoryViewModel.initialise(TestyRepositoryProvider(TestyGithubServiceProvider(), ""))

        // Add enough elements to trigger a next page request
        for (i in 1..5) {
            repositoryViewModel.repositoriesObservable?.add(RepositoryDisplayModel())
        }

        // Simulate scrolling through the list a few times
        for (i in 1..5) {
            for (j in 1..5) {
                repositoryViewModel.itemWillBeShown(j)
            }
        }

        // Only two page requests should have taken place, one when
        // initialised and one for requesting the next page
        assertEquals(2, repositoryViewModel.nextTimesCalled)
    }


    @Test
    fun testOnlyInitialisedOnce() {

        val repositoryViewModel = TestyRepositoryViewModel()

        val provider = TestyRepositoryProvider(TestyGithubServiceProvider(), "")
        repositoryViewModel.initialise(provider)
        repositoryViewModel.initialise(provider)
        repositoryViewModel.initialise(provider)

        // On initialisation the RepositoryViewModel makes a call to
        // refresh(). This should only happen once during a ViewModel object's lifetime.
        assertEquals(1, repositoryViewModel.refreshTimesCalled)
    }


    class TestyRepositoryViewModel : RepositoryViewModel() {

        var nextTimesCalled = 0
        var refreshTimesCalled = 0

        override fun getNextRepositories() {
            nextTimesCalled += 1
        }

        override fun refresh(repositoryProvider: CachedRepositoryProvider) {
            super.refresh(repositoryProvider)
            refreshTimesCalled += 1
        }
    }

    class TestyRepositoryProvider(gitHubServiceProvider: GitHubServiceProvider, gitHubUser: String) : CachedRepositoryProvider(gitHubServiceProvider, gitHubUser) {
        override fun hasNext(): Boolean {
            return true
        }
    }

    class TestyGithubServiceProvider : GitHubServiceProvider {
        override fun getGitHubService(): GitHubService {
            return object: GitHubService {
                override fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>> {
                    return Single.never()
                }
            }
        }
    }
}