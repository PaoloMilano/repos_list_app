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

    fun getRepos(user: String, page: Int, perPage: Int): Single<List<Repository>?> {
        return retrofit.create(GitHubService::class.java).getRepos(user, page, perPage)
    }

    fun recentRepos(page: Int, perPage: Int): Single<RepositoryResponse?> {
        return retrofit.create(GitHubService::class.java).recentRepos(page, perPage)
    }

    fun getUser(user: String): Single<GitHubUser?> {
        return retrofit.create(GitHubService::class.java).getUser(user)
    }
}