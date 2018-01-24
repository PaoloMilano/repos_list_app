package com.spacitron.reposlistapp.utils.custombindings

import android.databinding.BindingAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.spacitron.reposlistapp.utils.ItemSelectedListener
import com.spacitron.reposlistapp.utils.ItemShownListener
import com.squareup.picasso.Picasso

@BindingAdapter("imgSrc")
fun bindImageSource(view: ImageView, imgUrl: String?) {
    Picasso.with(view.context).load(imgUrl).into(view);
}

@BindingAdapter("itemShownListener")
fun bindOnItemShownListener(view: RecyclerView, onItemShownListener: ItemShownListener) {
    view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            (recyclerView?.layoutManager as? LinearLayoutManager)?.let {
                onItemShownListener.itemWillBeShown(it.findLastVisibleItemPosition())
            }
        }
    })
}

@BindingAdapter("hasNext")
fun <T> bindRecyclerViewHasNext(view: BoundRecyclerView<T>, hasNext: Boolean) {
    view.boundAdapter?.hasNext = hasNext
}


@BindingAdapter("onItemSelected")
fun <T> bindOnItemSelectedListener(view: BoundRecyclerView<T>, onItemSelectedListener: ItemSelectedListener<T>) {
    view.boundAdapter?.itemSelectedListener = onItemSelectedListener
}

@BindingAdapter("items")
fun <T> bindList(view: BoundRecyclerView<T>, list: List<T>?) {
    list?.let {
        view.boundAdapter?.setItems(it)
    }
}
