package com.spacitron.reposlistapp


import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spacitron.reposlistapp.databinding.RepoItemViewBinding
import com.spacitron.reposlistapp.model.Repository
import com.spacitron.reposlistapp.utils.ItemSelectedListener
import com.spacitron.reposlistapp.utils.collapse
import com.spacitron.reposlistapp.utils.expand
import kotlinx.android.synthetic.main.repo_item_view.view.*
import java.util.*


class ReposRecyclerViewAdapter : RecyclerView.Adapter< RecyclerView.ViewHolder>() {

    private val REPO_VIEW_TYPE = 0
    private val PLACEHOLDER_VIEW_TYPE = 1

    private var itemSelectedListener: ItemSelectedListener<Repository>? = null

    private val repositories: MutableList<Repository>


    var hasNext = false
    set(value) {
        field = value
        notifyItemChanged(itemCount)
    }

    init {
        repositories = ArrayList<Repository>()
    }

    override fun getItemCount(): Int {
        return if(hasNext) {
            repositories.size + 1
        }else{
            repositories.size
        }
    }


    fun setItems(repositories: List<Repository>) {
        this.repositories.clear()
        this.repositories.addAll(repositories)
        notifyDataSetChanged()
    }

    fun setItemSelectedListener(itemSelectedListener: ItemSelectedListener<Repository>){
        this.itemSelectedListener = itemSelectedListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= repositories.size){
            PLACEHOLDER_VIEW_TYPE
        }else{
            REPO_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == PLACEHOLDER_VIEW_TYPE){
            PlaceholdeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.placeholder_item_view, parent, false))
        }else{
            RepoViewHolder(DataBindingUtil.inflate<RepoItemViewBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.repo_item_view,
                    parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RepoViewHolder) {
            holder.bind(repositories[position])
        }
    }


    inner class PlaceholdeViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)

    inner class RepoViewHolder(private val binding: RepoItemViewBinding) : RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(repoItem: Repository) {

            binding.repository = repoItem
            binding.owner = repoItem.owner
            binding.onItemSelectedListener = itemSelectedListener
            binding.executePendingBindings()

            val descriptionBox = binding.root.description_box
            descriptionBox.visibility = View.GONE

            binding.root.info_butt.setOnClickListener{
                if (descriptionBox.visibility == View.VISIBLE) {
                    descriptionBox.collapse()
                }else{
                    descriptionBox.expand()
                }
            }
        }
    }
}
