package com.spacitron.reposlistapp.recentrepos

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.spacitron.reposlistapp.R
import com.spacitron.reposlistapp.userrepos.UserReposActivity
import kotlinx.android.synthetic.main.activity_user_search.*


class RecentReposListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_user_search)
        setSupportActionBar(recent_repos_list_toolbar)

        supportFragmentManager.beginTransaction().replace(recent_repos_list_container.id, RecentRepoListFragment.newInstance()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            R.id.search_menu_item -> {
                onSearchRequested()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent?.getAction()) {
            intent?.getStringExtra(SearchManager.QUERY).let {
                startActivity(UserReposActivity.getParamIntent(this, it))
            }
        }
    }
}
