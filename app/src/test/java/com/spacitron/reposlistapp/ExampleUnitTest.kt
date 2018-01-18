package com.spacitron.reposlistapp

import io.reactivex.Maybe
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testPaginationEnds() {

        val endedRepositoryProvider = RepositoryProvider(object:  GitHubServiceProvider {

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

        val unfinishedRepositoryProvider = RepositoryProvider(object:  GitHubServiceProvider {

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
}
