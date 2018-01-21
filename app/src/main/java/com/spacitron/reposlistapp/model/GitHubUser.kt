package com.spacitron.reposlistapp.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class GitHubUser: RealmModel {

    @PrimaryKey
    var id: Long? = null

    var login: String? = null

    var name: String? = null

    @SerializedName("avatar_url")
    var avatarUrl: String? = null

    @SerializedName("html_url")
    var htmlUrl: String? = null

    var company: String? = null
}