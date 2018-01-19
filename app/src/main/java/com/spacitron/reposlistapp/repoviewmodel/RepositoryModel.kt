package com.spacitron.reposlistapp.repoviewmodel

import com.spacitron.reposlistapp.model.GitHubUser
import com.spacitron.reposlistapp.model.Repository

/**
 * We need to let the client have different display objects depending on whether we're at
 * the end of the repo list or not. These classes are necessary because we cannot extend Realm data classes.
 */
open class RepositoryDisplayModel

class RepositoryModel(val repository: Repository) : RepositoryDisplayModel() {
    val id: Long? get() = repository.id
    val name: String? get() = repository.name
    val fullName: String? get() = repository.fullName
    val htmlUrl: String? get() = repository.htmlUrl
    val description: String? get() = repository.description
    val owner: GitHubUser? get() = repository.owner
}

object PlaceholderRepositoryModel : RepositoryDisplayModel()

