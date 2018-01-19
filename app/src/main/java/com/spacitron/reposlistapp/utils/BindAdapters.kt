package com.spacitron.reposlistapp.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.spacitron.reposlistapp.ReposRecyclerViewAdapter
import com.spacitron.reposlistapp.model.Repository
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
