package com.spacitron.reposlistapp.model

open class Repository{

    var id: Long? = null
    var name: String? = null
    var full_name: String? = null
    var html_url: String? = null
    var description: String?= null
    var owner: GitHubUser? = null

}