package com.spacitron.reposlistapp.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.spacitron.reposlistapp.ReposRecyclerViewAdapter
import com.spacitron.reposlistapp.repoviewmodel.RepositoryDisplayModel
import com.spacitron.reposlistapp.repoviewmodel.RepositoryModel
import com.squareup.picasso.Picasso

@BindingAdapter("imgSrc")
fun bindImageSource(view: ImageView, imgUrl: String?) {
    Picasso.with(view.context).load(imgUrl).into(view);
}

@BindingAdapter("itemShownListener")
fun bindOnItemShownListener(view: RecyclerView, onItemShownListener: ItemShownListener) {
    view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            val layoutManager = LinearLayoutManager::class.java.cast(recyclerView!!.layoutManager)
            val lastVisible = layoutManager.findLastVisibleItemPosition()
            onItemShownListener.itemWillBeShown(lastVisible)
        }
    })
}

@BindingAdapter("onItemSelected")
fun bindOnItemSelectedListener(view: RecyclerView, onItemSelectedListener: ItemSelectedListener<RepositoryModel>) {
    val adapter = view.adapter
    if (adapter is ReposRecyclerViewAdapter) {
        adapter.setItemSelectedListener(onItemSelectedListener)
    }
}

@BindingAdapter("items")
fun bindList(view: RecyclerView, list: List<RepositoryDisplayModel>?) {
    if (view.adapter == null) {
        val layoutManager = LinearLayoutManager(view.context)
        view.layoutManager = layoutManager
        view.adapter = ReposRecyclerViewAdapter()
    }
    if (list != null) {
        (view.adapter as ReposRecyclerViewAdapter).setItems(list)
    }
}
