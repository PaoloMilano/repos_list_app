package com.spacitron.reposlistapp.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Repository: RealmModel{

    @PrimaryKey
    var id: Long? = null

    var name: String? = null
    var description: String?= null

    var owner: GitHubUser? = null

    @SerializedName("full_name")
    var fullName: String? = null

    @SerializedName("html_url")
    var htmlUrl: String? = null

}