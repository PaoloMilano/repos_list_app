package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.userrepos.repoviewmodel.CachedRepositoryManager
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

        val endedRepositoryProvider = TestyCachedRepositoryManager(object : GitHubServiceProvider {

            override fun getGitHubService(): GitHubService {

                return object : GitHubService {

                    override fun getUser(user: String): Single<GitHubUser?> {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {

                        return Single.just(listOf(Repository()))

                    }
                }
            }
        }, "")

        endedRepositoryProvider.getNextRepos().subscribe()

        // The service returned fewer items than expected. We can assume there's no more to fetch.
        assertFalse(endedRepositoryProvider.hasNext())
    }


    @Test
    fun testPaginationContinues() {

        val unfinishedRepositoryProvider = TestyCachedRepositoryManager(object : GitHubServiceProvider {

            override fun getGitHubService(): GitHubService {

                return object : GitHubService {

                    override fun getUser(user: String): Single<GitHubUser?> {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {

                        return Single.just(listOf(Repository(),Repository()))

                    }
                }
            }
        }, "", 2)


        unfinishedRepositoryProvider.getNextRepos().subscribe()

        // The service returned the same number of items we requested (2).
        // This means there could be more so ensure that pagination has not yet ended.
        assertTrue(unfinishedRepositoryProvider.hasNext())
    }

    @Test
    fun testFallsBackToCacheOnError() {

        val repositoryProvider = TestyCachedRepositoryManager(object : GitHubServiceProvider {
            override fun getGitHubService(): GitHubService {
                return object : GitHubService {

                    override fun getUser(user: String): Single<GitHubUser?> {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {
                        return Single.error(Exception())
                    }
                }
            }
        }, "")

        repositoryProvider.getNextRepos().subscribe()

        // Check that we called the cache when an error was thrown
        assertEquals(1, repositoryProvider.timesCalled)
    }


    class TestyCachedRepositoryManager(gitHubServiceProvider: GitHubServiceProvider, gitHubUser: String, itemsPerPage: Int = 15) : CachedRepositoryManager(gitHubServiceProvider, gitHubUser, itemsPerPage) {

        var timesCalled = 0

        override fun getFromCache(): List<Repository> {
            timesCalled +=1
            return ArrayList()
        }

        // Override this to ensure we don't touch Realm and prevent exceptions being thrown
        override fun saveReposToCache(repos: List<Repository>) {

        }
    }
}
