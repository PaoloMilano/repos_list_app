package com.spacitron.reposlistapp

import com.spacitron.reposlistapp.githubservice.GitHubWebRepoDeserialiser
import org.junit.Test

class WebReposTest{

    @Test
    fun testReposParsing(){
        var trendingReposString = javaClass.getResourceAsStream("/trendingrepos").bufferedReader().use { it.readText() }
        GitHubWebRepoDeserialiser.map(trendingReposString)
    }
}