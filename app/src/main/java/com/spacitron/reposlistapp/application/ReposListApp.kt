package com.spacitron.reposlistapp.application

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class ReposListApp: Application(){
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        // We're only providing limited offline caching so it should be no problem
        // if we lose the local copy of this data
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())
    }
}