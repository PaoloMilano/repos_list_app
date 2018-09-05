package com.spacitron.reposlistapp.githubservice

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.model.RepositoryResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object GitHubServiceProvider {

    private val retrofit = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().addInterceptor {
                // Keep this here for easy debugging
                it.proceed(it.request())
            }.build())
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()

    private val webRetrofit = Retrofit.Builder()
            .client(OkHttpClient().newBuilder().addInterceptor {
                // Keep this here for easy debugging
                it.proceed(it.request())
            }.build())
            .baseUrl("https://github.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()


    // This is so we can return a different service for testing purposes
    open fun getGitHubService(): GitHubService{
        return retrofit.create(GitHubService::class.java)
    }

    fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {
        return getGitHubService().getRepos(user, page, perPage)
    }

    fun recentRepos(page: Int, perPage: Int): Single<RepositoryResponse?> {
        return getGitHubService().recentRepos(page, perPage)
    }

    fun getUser(user: String): Single<GitHubUser?> {
        return getGitHubService().getUser(user)
    }

    fun getTrendingRepos(): Single<List<Repository>?> {
        return webRetrofit.create(GitHubWebService::class.java).getTrendingRepos().map { GitHubWebRepoDeserialiser.map(String(it.bytes()))}
    }
}