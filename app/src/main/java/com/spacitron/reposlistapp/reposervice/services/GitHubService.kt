package com.spacitron.reposlistapp.reposervice.services

import com.spacitron.reposlistapp.model.Repository
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface GitHubService {

    @GET("/users/{user}/repos")
    fun getRepos(@Path("user") user: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Single<List<Repository>>

}