package com.spacitron.reposlistapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.spacitron.reposlistapp.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso


@BindingAdapter("bind:imgSrc")
fun bindImageSource(view: ImageView, imgUrl: String) {
    Picasso.with(view.context).load(imgUrl).into(view);
}

@BindingAdapter("bind:onItemSelected")
fun bindOnItemSelected(view: RecyclerView, onItemSelectedListener: ItemSelectedListener<Repository>) {
    val adapter = view.adapter
    if (adapter is ReposRecyclerViewAdapter) {
        adapter.setItemSelectedListener(onItemSelectedListener)
    }
}

@BindingAdapter("bind:items")
fun bindList(view: RecyclerView, list: List<Repository>?) {
    if (view.adapter == null) {
        val layoutManager = LinearLayoutManager(view.context)
        view.layoutManager = layoutManager
        view.adapter = ReposRecyclerViewAdapter()
    }
    if (list != null) {
        (view.adapter as ReposRecyclerViewAdapter).setItems(list)
    }
}


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repositoryViewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.repoViewModel = repositoryViewModel

        repositoryViewModel.initialise(RepositoryProvider(RetrofitGitHubServiceProvider(), "JakeWharton"))

        val observableCitySelection = repositoryViewModel.itemSelected
        observableCitySelection.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(p0: Observable?, p1: Int) {

                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(observableCitySelection.get().html_url)
                        startActivity(i)

                    }
                })
    }
}
