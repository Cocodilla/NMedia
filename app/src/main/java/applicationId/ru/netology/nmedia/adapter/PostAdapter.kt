package applicationId.ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.CardPostBinding
import applicationId.ru.netology.nmedia.dto.Post

class PostAdapter(
    private val interactionListener: OnInteractionListener
) : ListAdapter<Post, PostAdapter.ViewHolder>(PostDiffCallback()) {

    interface OnInteractionListener {
        fun onLike(post: Post)
        fun onShare(post: Post)
        fun onRemove(post: Post)
        fun onEdit(post: Post)
        fun onVideoPlay(post: Post)
        fun onPostClick(post: Post)
    }

    inner class ViewHolder(
        private val binding: CardPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentPost: Post? = null

        init {
            binding.root.setOnClickListener {
                currentPost?.let { post ->
                    interactionListener.onPostClick(post)
                }
            }

            binding.like.setOnClickListener {
                currentPost?.let { post ->
                    interactionListener.onLike(post)
                }
            }

            binding.share.setOnClickListener {
                currentPost?.let { post ->
                    interactionListener.onShare(post)
                }
            }

            binding.menu.setOnClickListener {
                currentPost?.let { post ->
                    showMenu(post, it)
                }
            }

            binding.videoGroup.setOnClickListener {
                currentPost?.let { post ->
                    if (!post.video.isNullOrEmpty()) {
                        interactionListener.onVideoPlay(post)
                    }
                }
            }

            binding.playButton.setOnClickListener {
                currentPost?.let { post ->
                    if (!post.video.isNullOrEmpty()) {
                        interactionListener.onVideoPlay(post)
                    }
                }
            }
        }

        fun bind(post: Post) {
            currentPost = post
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                like.isChecked = post.likedByMe
                like.text = post.likes.toString()
                share.text = post.shares.toString()
                views.text = post.views.toString()

                if (post.video.isNullOrEmpty()) {
                    videoGroup.visibility = View.GONE
                } else {
                    videoGroup.visibility = View.VISIBLE
                }
            }
        }

        private fun showMenu(post: Post, view: View) {
            PopupMenu(view.context, view).apply {
                inflate(R.menu.menu_post)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.edit -> {
                            interactionListener.onEdit(post)
                            true
                        }
                        R.id.remove -> {
                            interactionListener.onRemove(post)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}