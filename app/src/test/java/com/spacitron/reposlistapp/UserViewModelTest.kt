package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.reposervice.serviceproviders.GitHubServiceProvider
import com.spacitron.reposlistapp.reposervice.services.GitHubService
import com.spacitron.reposlistapp.userviewmodel.GitHubUserViewModel
import io.reactivex.Single
import junit.framework.Assert
import org.junit.Test

class UserViewModelTest {


    @Test
    fun testUsernameIsSetOnError() {

        val testName = "test name"

        val userViewModel = TestyGitHubUserViewModel()

        userViewModel.getUserSingle(object : GitHubServiceProvider{
            override fun getGitHubService(): GitHubService {
                return object : GitHubService{

                    override fun getUser(user: String): Single<GitHubUser?> {
                        return Single.error(Exception())
                    }

                    override fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }
            }

        }, testName).subscribe{ gituser->
            // In case of error we set the user's name to the login string we initially passed
            // that way the client will have something somwhat relevant to show the user
            Assert.assertEquals(testName, gituser?.name)
        }
    }

    class TestyGitHubUserViewModel: GitHubUserViewModel(){
        override public fun getUserSingle(gitHubServiceProvider: GitHubServiceProvider, gitHubUserLogin: String): Single<GitHubUser?> {
            return super.getUserSingle(gitHubServiceProvider, gitHubUserLogin)
        }

        override fun getFromCache(gitHubUserLogin: String): GitHubUser? {
            return null
        }
    }
}