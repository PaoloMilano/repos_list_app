package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.repoviewmodel.CachedRepositoryManager
import com.spacitron.reposlistapp.repoviewmodel.RepositoryViewModel
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Test

class RepositoryViewModelTest {


    @Test
    fun testPageOnlyCalledOnce() {

        val testyRepositoryServiceProvider = TestyRepositoryProvider(TestyGithubServiceProvider(), "")

        val repositoryViewModel = TestyRepositoryViewModel()
        repositoryViewModel.initialise(testyRepositoryServiceProvider)

        // Add enough elements to trigger a next page request
        for (i in 1..5) {
            repositoryViewModel.repositoriesObservable?.add(Repository())
        }

        // Simulate scrolling through the list a few times
        for (i in 1..5) {
            for (j in 1..5) {
                repositoryViewModel.itemWillBeShown(j)
            }
        }

        // Only two page requests should have taken place, one when
        // initialised and one for requesting the next page
        assertEquals(2, testyRepositoryServiceProvider.getNextReposTimesCalled)
    }

   @Test
    fun testPageCalledEvenWithSkippedItems() {

       // Sometimes the scroll listener does not receive events for every single item
       // being shown. The view model should be able to hanbdle this.
        val testyRepositoryServiceProvider = TestyRepositoryProvider(TestyGithubServiceProvider(), "")

        val repositoryViewModel = TestyRepositoryViewModel()
        repositoryViewModel.initialise(testyRepositoryServiceProvider)

        // Add enough elements to trigger a next page request
        for (i in 1..5) {
            repositoryViewModel.repositoriesObservable?.add(Repository())
        }

       //Immediately signal that the last item was shown
       repositoryViewModel.itemWillBeShown(5)

       // The viewmodel should have asked for a new page even though we didn't hit the n-3 element
       assertEquals(2, testyRepositoryServiceProvider.getNextReposTimesCalled)


       // Now scroll from top to bottom
        for (i in 1..5) {
            for (j in 1..5) {
                repositoryViewModel.itemWillBeShown(j)
            }
        }

       // The viewmodel should have not asked for a new page in response to scrolling the same list twice
       assertEquals(2, testyRepositoryServiceProvider.getNextReposTimesCalled)

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

        var refreshTimesCalled = 0

        override fun refresh(repositoryProvider: CachedRepositoryManager) {
            super.refresh(repositoryProvider)
            refreshTimesCalled += 1
        }
    }

    class TestyRepositoryProvider(gitHubServiceProvider: GitHubServiceProvider, gitHubUser: String) : CachedRepositoryManager(gitHubServiceProvider, gitHubUser) {

        var getNextReposTimesCalled = 0

        override fun getNextRepos(): Single<List<Repository>?> {
            getNextReposTimesCalled+=1
            return super.getNextRepos()
        }

        override fun hasNext(): Boolean {
            return true
        }
    }

    class TestyGithubServiceProvider : GitHubServiceProvider {
        override fun getGitHubService(): GitHubService {
            return object: GitHubService {
                override fun getUser(user: String): Single<GitHubUser?> {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {
                    return Single.never()
                }
            }
        }
    }
}