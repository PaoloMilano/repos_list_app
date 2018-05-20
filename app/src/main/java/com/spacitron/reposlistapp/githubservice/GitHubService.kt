package com.spacitron.reposlistapp.githubservice

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.model.RepositoryResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface GitHubService {

    @GET("/users/{user}/repos")
    fun getRepos(@Path("user") user: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Single<List<Repository>?>

    // https://help.github.com/articles/searching-repositories/#search-based-on-when-a-repository-was-created-or-last-updated
    @GET("/search/repositories")
    fun recentRepos(@Query("page") page: Int, @Query("per_page") perPage: Int, @Query("q") pushedDateParam: String? ="pushed:>2008-02-25", @Query("sort") sort:String? = "updated", @Query("order") order: String? = "desc"): Single<RepositoryResponse?>

    @GET("/users/{user}")
    fun getUser(@Path("user") user: String): Single<GitHubUser?>

}