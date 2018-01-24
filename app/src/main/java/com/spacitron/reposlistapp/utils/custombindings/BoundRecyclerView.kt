package com.spacitron.reposlistapp.utils.custombindings

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.spacitron.reposlistapp.utils.ItemSelectedListener

/**
 * Custom RecyclerView that uses generics to enable reuse with data bindings
 */
class BoundRecyclerView<T>(context:Context, attrs: AttributeSet, defStyle: Int) : RecyclerView(context, attrs) {

    var boundAdapter: BoundPagedRecyclerViewAdapter<T, *>?
    set(value) {
        value?.let {
            adapter = it
        }
    }

    get() {
        if (adapter is BoundPagedRecyclerViewAdapter<*, *>){
            return adapter as BoundPagedRecyclerViewAdapter<T, *>?
        }
        return null
    }
    constructor(context:Context, attrs: AttributeSet) : this(context, attrs, 0)
}

/**
 * Custom Adapter that uses generics to enable reuse with data bindings. This adapter is low on functionality, limiting
 * itself to providing fields for data to be updates while letting subclasses decide how to answer update events
 */
abstract class BoundPagedRecyclerViewAdapter<T, VH: RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {

    val itemList = ArrayList<T>()

    open var hasNext = false

    var itemSelectedListener: ItemSelectedListener<T>? = null
        set(value) { field = value }


    fun getItem(position: Int): T?{
        return if(position< itemList.size){
            itemList.get(position)
        }else{
            null
        }
    }


    open fun setItems(items:List<T>){
        itemList.clear()
        itemList.addAll(items)
    }

}