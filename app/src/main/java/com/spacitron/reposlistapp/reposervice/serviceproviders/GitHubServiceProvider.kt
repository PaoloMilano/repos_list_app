package com.spacitron.reposlistapp.reposervice.serviceproviders

import com.spacitron.reposlistapp.reposervice.services.GitHubService

interface GitHubServiceProvider{
    fun getGitHubService(): GitHubService
}