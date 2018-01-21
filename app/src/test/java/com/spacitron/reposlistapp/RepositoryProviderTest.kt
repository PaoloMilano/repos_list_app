package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.repoviewmodel.CachedRepositoryProvider
import io.reactivex.Maybe
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

        val endedRepositoryProvider = CachedRepositoryProvider(object : GitHubServiceProvider {

            override fun getGitHubService(): GitHubService {

                return object : GitHubService {

                    override fun getRepos(user: String, page: Int, perPage: Int): Maybe<List<Repository>> {

                        val repo = Repository()
                        repo.id = 123
                        return Maybe.just(listOf(repo))

                    }
                }
            }
        }, "")


        endedRepositoryProvider.getNextReposMaybe().subscribe()

        // The service returned fewer items than expected. We can assume there's no more to fetch.
        assertFalse(endedRepositoryProvider.hasNext())
    }


    @Test
    fun testPaginationContinues() {

        val unfinishedRepositoryProvider = CachedRepositoryProvider(object : GitHubServiceProvider {

            override fun getGitHubService(): GitHubService {

                return object : GitHubService {

                    override fun getRepos(user: String, page: Int, perPage: Int): Maybe<List<Repository>> {

                        val repo1 = Repository()
                        repo1.id = 123

                        val repo2 = Repository()
                        repo2.id = 456
                        return Maybe.just(listOf(repo1, repo2))

                    }
                }
            }
        }, "", 2)


        unfinishedRepositoryProvider.getNextReposMaybe().subscribe()

        // The service returned the same number of items we requested. There could be more so this has not yet ended.
        assertTrue(unfinishedRepositoryProvider.hasNext())
    }

    @Test
    fun testFallsBackToCacheOnError() {

        val repositoryProvider = TestyCachedRepositoryProvider(object : GitHubServiceProvider {
            override fun getGitHubService(): GitHubService {
                return object : GitHubService {
                    override fun getRepos(user: String, page: Int, perPage: Int): Maybe<List<Repository>> {
                        return Maybe.error(Exception())
                    }
                }
            }
        }, "")

        repositoryProvider.getNextReposMaybe().subscribe()

        // Check that we called the cache when an error was thrown
        assertEquals(1, repositoryProvider.timesCalled)
    }


    class TestyCachedRepositoryProvider(gitHubServiceProvider: GitHubServiceProvider, gitHubUser: String) : CachedRepositoryProvider(gitHubServiceProvider, gitHubUser) {

        var timesCalled = 0

        override fun getFromCache(): List<Repository> {
            timesCalled +=1
            return ArrayList()
        }
    }
}
