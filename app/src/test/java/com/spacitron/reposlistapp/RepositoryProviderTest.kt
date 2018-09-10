package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposlistviewmodel.CachedRepositoryManager
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RepositoryProviderTest {


    @Test
    fun testPaginationEnds() {
        val endedRepositoryProvider = TestyCachedRepositoryManager({ _: Int, _: Int -> Single.just(listOf(Repository())) }, 100)
        endedRepositoryProvider.getNextRepos().subscribe()

        // We asked for 100 repos but the service returned fewer items than expected. We can assume there's no more to fetch.
        assertFalse(endedRepositoryProvider.hasNext())
    }


    @Test
    fun testPaginationContinues() {
        val unfinishedRepositoryProvider = TestyCachedRepositoryManager({ _: Int, _: Int -> Single.just(listOf(Repository(), Repository())) }, 2)
        unfinishedRepositoryProvider.getNextRepos().subscribe()

        // The service returned the same number of items we requested (2).
        // This means there could be more so ensure that pagination has not yet ended.
        assertTrue(unfinishedRepositoryProvider.hasNext())
    }

    @Test
    fun testFallsBackToCacheOnError() {
        val repositoryProvider = TestyCachedRepositoryManager({ _: Int, _: Int -> Single.error(Exception()) }, 10)
        repositoryProvider.getNextRepos().subscribe()

        // Check that we called the cache when an error was thrown
        assertEquals(1, repositoryProvider.timesCalled)
    }


    class TestyCachedRepositoryManager(repoListFetcher: (page: Int, perPage: Int) -> Single<List<Repository>?>, items: Int) : CachedRepositoryManager(repoListFetcher, items) {

        var timesCalled = 0

        override fun getFromCache(): List<Repository> {
            timesCalled += 1
            return ArrayList()
        }

        // Override this to ensure we don't touch Realm and prevent exceptions being thrown
        override fun saveReposToCache(repos: List<Repository>) {

        }
    }
}
