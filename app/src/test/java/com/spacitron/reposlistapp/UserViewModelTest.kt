package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.githubservice.GitHubService
import com.spacitron.reposlistapp.githubservice.GitHubServiceProvider
import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.model.RepositoryResponse
import com.spacitron.reposlistapp.userrepos.userviewmodel.GitHubUserViewModel
import io.reactivex.Single
import junit.framework.Assert
import org.junit.Test

class UserViewModelTest {


    @Test
    fun testUsernameIsSetOnError() {

        val testName = "test name"

        // Prepare the service to ensure it'll throw an error
        val mockGithubService = object : GitHubService {
            override fun recentRepos(page: Int, perPage: Int, pushedDateParam: String?, sort: String?, order: String?): Single<RepositoryResponse?> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getUser(user: String): Single<GitHubUser?> {
                return Single.error(Exception())
            }

            override fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
        // Use the service to configure the service provider
        GitHubServiceProvider.initialise(mockGithubService, null)

        val userViewModel = TestyGitHubUserViewModel()
        userViewModel.getUserSingle(mockGithubService::getUser, testName).subscribe{ gituser->
            // In case of error we set the user's name to the login string we initially passed
            // that way the client will have something somwhat relevant to show the user
            Assert.assertEquals(testName, gituser?.name)
        }
    }

    class TestyGitHubUserViewModel: GitHubUserViewModel(){
        public override fun getUserSingle(getUserFunction: (gitHubUserLogin: String) -> Single<GitHubUser?>, gitHubUserLogin: String): Single<GitHubUser?> {
            return super.getUserSingle(getUserFunction, gitHubUserLogin)
        }

        override fun getFromCache(gitHubUserLogin: String): GitHubUser? {
            return null
        }
    }
}