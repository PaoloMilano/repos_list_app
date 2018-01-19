package com.spacitron.reposlistapp


import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.spacitron.reposlistapp.databinding.RepoItemViewBinding
import com.spacitron.reposlistapp.repoviewmodel.PlaceholderRepositoryModel
import com.spacitron.reposlistapp.repoviewmodel.RepositoryDisplayModel
import com.spacitron.reposlistapp.repoviewmodel.RepositoryModel
import com.spacitron.reposlistapp.utils.AnimationEndedListener
import com.spacitron.reposlistapp.utils.ItemSelectedListener
import com.spacitron.reposlistapp.utils.collapse
import com.spacitron.reposlistapp.utils.expand
import kotlinx.android.synthetic.main.repo_item_view.view.*
import java.util.*


class ReposRecyclerViewAdapter : RecyclerView.Adapter< RecyclerView.ViewHolder>() {


    private val IMG_ANIMATION_TIME: Long = 40

    private val REPO_VIEW_TYPE = 0
    private val PLACEHOLDER_VIEW_TYPE = 1

    private val repositories: MutableList<RepositoryDisplayModel>
    private var itemSelectedListener: ItemSelectedListener<RepositoryModel>? = null

    init {
        repositories = ArrayList<RepositoryDisplayModel>()
    }

    override fun getItemCount(): Int {
        return repositories.size
    }


    fun setItems(repositories: List<RepositoryDisplayModel>) {
        this.repositories.clear()
        this.repositories.addAll(repositories)
        notifyDataSetChanged()
    }

    fun setItemSelectedListener(itemSelectedListener: ItemSelectedListener<RepositoryModel>){
        this.itemSelectedListener = itemSelectedListener
    }

    override fun getItemViewType(position: Int): Int {
        return if (repositories.get(position) is PlaceholderRepositoryModel){
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
            holder.bind(repositories[position] as RepositoryModel)
        }
    }


    inner class PlaceholdeViewHolder(private val rootView: View) : RecyclerView.ViewHolder(rootView)

    inner class RepoViewHolder(private val binding: RepoItemViewBinding) : RecyclerView.ViewHolder(binding.getRoot()) {

        fun bind(repoItem: RepositoryModel) {

            binding.repositoryModel = repoItem
            binding.owner = repoItem.owner
            binding.onItemSelectedListener = itemSelectedListener
            binding.executePendingBindings()

            val descriptionBox = binding.root.description_box
            descriptionBox.visibility = View.GONE

            binding.root.info_butt.setOnClickListener{
                if (descriptionBox.visibility == View.VISIBLE) {

                    // Manage the visibility of the avatar separately or it'll lok weird as it expands and contracts
                    binding.root.avatar.animate().alpha(0f).setDuration(IMG_ANIMATION_TIME).start()
                    descriptionBox.collapse()

                }else{

                    descriptionBox.expand(object: AnimationEndedListener {
                        override fun animationStatus(status: AnimationEndedListener.AnimationStatus) {
                            if(status == AnimationEndedListener.AnimationStatus.COMPLETED){
                                // Manage the visibility of the avatar separately or it'll lok weird as it expands and contracts
                                binding.root.avatar.animate().alpha(1f).start()
                            }
                        }
                    })
                }
            }
        }
    }
}
