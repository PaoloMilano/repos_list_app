package com.spacitron.reposlistapp

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposlistviewmodel.CachedRepositoryManager
import io.reactivex.Single
import io.realm.Realm
import junit.framework.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CachedRepositoryManagerTest {

    val getTestRepos = { page: Int, perPage: Int ->
        val repo1 = Repository()
        repo1.id = 1

        val repo2 = Repository()
        repo2.id = 2

        val repo3 = Repository()
        repo3.id = 3

        val repo4 = Repository()
        repo4.id = 4

        val repo5 = Repository()
        repo5.id = 5

        val repos = listOf(repo1, repo2, repo3, repo4, repo5)
        Single.just(repos.subList(0, if (perPage > repos.size) {
            repos.size
        } else {
            perPage
        }))
    }

    companion object {
        @BeforeClass
        fun setUp() {
            Realm.init(InstrumentationRegistry.getTargetContext())
        }
    }

    init {

    }

    @Test
    fun testCacheReturnsRightValues() {

        val repo1 = Repository()
        repo1.id = 11

        val repo2 = Repository()
        repo2.id = 22

        val repo3 = Repository()
        repo3.id = 33

        val testyRepoManager = TestyCachedRepositoryManager(getTestRepos, 3)
        testyRepoManager.saveReposToCache(listOf(repo1, repo2, repo3))

        val repos = testyRepoManager.getFromCache()

        assertEquals(3, repos.size)

        assertTrue(repos.firstOrNull { it.id == repo1.id } != null)
        assertTrue(repos.firstOrNull { it.id == repo2.id } != null)
        assertTrue(repos.firstOrNull { it.id == repo3.id } != null)
    }


    @Test
    fun testPaging() {

        // Let's instruct the manager to return 3 items per page
        val testyRepoManager = TestyCachedRepositoryManager(getTestRepos, 3)
        testyRepoManager.getNextRepos().subscribe { t: List<Repository>? ->
            assertEquals(3, t?.size)
        }

        //Since the service returned 3 items the manager should assume there's another page available
        assertTrue(testyRepoManager.hasNext())


        // Next let's instruct the manager to return 6 items per page
        val testyRepoManager2 = TestyCachedRepositoryManager(getTestRepos, 6)
        testyRepoManager2.getNextRepos().subscribe { t: List<Repository>? -> assertEquals(5, t?.size) }

        //Since the service returned only 5 items the manager should assume there are no more pages to fetch
        assertFalse(testyRepoManager2.hasNext())
    }


    class TestyCachedRepositoryManager(getTestRepos: (page:Int, perPage:Int)-> Single<List<Repository>?>,itemsPerPage: Int? = 0) : CachedRepositoryManager(getTestRepos, itemsPerPage ?: 0) {

        override public fun saveReposToCache(repos: List<Repository>) {
            super.saveReposToCache(repos)
        }

        override public fun getFromCache(): List<Repository> {
            return super.getFromCache()
        }
    }
}