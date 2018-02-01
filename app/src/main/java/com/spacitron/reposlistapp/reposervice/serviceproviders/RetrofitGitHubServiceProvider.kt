package com.spacitron.reposlistapp.reposervice.serviceproviders

import com.spacitron.reposlistapp.reposervice.services.GitHubService
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitGitHubServiceProvider: GitHubServiceProvider {

    val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()


    override fun getGitHubService(): GitHubService {
        return retrofit.create(GitHubService::class.java)
    }

}