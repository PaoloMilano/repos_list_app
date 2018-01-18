package com.spacitron.reposlistapp


import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.spacitron.reposlistapp.databinding.RepoItemViewBinding
import kotlinx.android.synthetic.main.repo_item_view.view.*
import java.util.*


class ReposRecyclerViewAdapter : RecyclerView.Adapter<ReposRecyclerViewAdapter.RepoViewHolder>() {


    private val IMG_ANIMATION_TIME: Long = 40
    private val repositories: MutableList<Repository>
    private var itemSelectedListener: ItemSelectedListener<Repository>? = null

    init {
        repositories = ArrayList<Repository>()
    }

    override fun getItemCount(): Int {
        return repositories.size
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
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposRecyclerViewAdapter.RepoViewHolder {
        return RepoViewHolder(DataBindingUtil.inflate<RepoItemViewBinding>(
                LayoutInflater.from(parent.context),
                R.layout.repo_item_view,
                parent, false))
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }


    inner class RepoViewHolder(private val binding: RepoItemViewBinding) : RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(repoItem: Repository) {

            binding.repository = repoItem
            binding.owner = repoItem.owner
            binding.onItemSelectedListener = itemSelectedListener
            binding.executePendingBindings()

            binding.root.info_butt.setOnClickListener{
                val descriptionBox = binding.root.description_box
                if (descriptionBox.visibility == View.VISIBLE) {

                    // Manage the visibility of the avatar separately or it'll lok weird as it expands and contracts
                    binding.root.avatar.animate().alpha(0f).setDuration(IMG_ANIMATION_TIME).start()
                    ExpandableLayoutExpander.collapse(descriptionBox)

                }else{
                    ExpandableLayoutExpander.expand(descriptionBox, object : Animation.AnimationListener{
                        override fun onAnimationRepeat(animation: Animation?) {
                        }

                        override fun onAnimationStart(animation: Animation?) {
                        }

                        override fun onAnimationEnd(animation: Animation?) {

                            // Manage the visibility of the avatar separately or it'll lok weird as it expands and contracts
                            binding.root.avatar.animate().alpha(1f).start()
                        }

                    })
                }
            }
        }
    }
}
