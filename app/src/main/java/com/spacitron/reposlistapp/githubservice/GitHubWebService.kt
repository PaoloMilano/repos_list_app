package com.spacitron.reposlistapp.githubservice

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET


interface GitHubWebService {

    @GET("/trending")
    fun getTrendingRepos(): Single<ResponseBody?>

}