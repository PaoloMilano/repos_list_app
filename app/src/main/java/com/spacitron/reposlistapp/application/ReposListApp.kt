package com.spacitron.reposlistapp.application

import android.app.Application
import com.spacitron.reposlistapp.githubservice.GitHubService
import com.spacitron.reposlistapp.githubservice.GitHubServiceProvider
import com.spacitron.reposlistapp.githubservice.GitHubWebService
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ReposListApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        // We're only providing limited offline caching so it should be no problem
        // if we lose the local copy of this data
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())


        val gitHubService = Retrofit.Builder()
                .client(OkHttpClient().newBuilder().addInterceptor {
                    // Keep this here for easy debugging
                    it.proceed(it.request())
                }.build())
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build().create(GitHubService::class.java)

        val gitHubWebService = Retrofit.Builder()
                .client(OkHttpClient().newBuilder().addInterceptor {
                    // Keep this here for easy debugging
                    it.proceed(it.request())
                }.build())
                .baseUrl("https://github.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build().create(GitHubWebService::class.java)
        GitHubServiceProvider.initialise(gitHubService, gitHubWebService)
    }
}