package applicationId.ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.CardPostBinding
import applicationId.ru.netology.nmedia.dto.Attachment
import applicationId.ru.netology.nmedia.dto.Post
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PostAdapter(
    private val interactionListener: OnInteractionListener
) : PagingDataAdapter<Post, PostAdapter.ViewHolder>(PostDiffCallback()) {

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

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let(interactionListener::onPostClick)
            }
            binding.like.setOnClickListener {
                getItem(bindingAdapterPosition)?.let(interactionListener::onLike)
            }
            binding.share.setOnClickListener {
                getItem(bindingAdapterPosition)?.let(interactionListener::onShare)
            }
            binding.menu.setOnClickListener { v ->
                getItem(bindingAdapterPosition)?.let { post -> showMenu(post, v) }
            }
            binding.videoGroup.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { post ->
                    if (!post.video.isNullOrEmpty()) interactionListener.onVideoPlay(post)
                }
            }
            binding.playButton.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { post ->
                    if (!post.video.isNullOrEmpty()) interactionListener.onVideoPlay(post)
                }
            }
        }

        fun bind(post: Post) = with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            like.isChecked = post.likedByMe
            like.text = post.likes.toString()
            share.text = post.shares.toString()
            views.text = post.views.toString()

            videoGroup.visibility = if (post.video.isNullOrEmpty()) View.GONE else View.VISIBLE
            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.GONE

            val avatarUrl = post.authorAvatar?.let { fileName ->
                "${BASE_URL}avatars/$fileName"
            }

            Glide.with(avatar)
                .load(avatarUrl)
                .timeout(10_000)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .into(avatar)

            val attachment = post.attachment
            if (attachment != null && attachment.type == Attachment.AttachmentType.IMAGE) {
                attachmentGroup.visibility = View.VISIBLE
                val imageUrl = "${BASE_URL}images/${attachment.url}"
                Glide.with(attachmentImage)
                    .load(imageUrl)
                    .timeout(10_000)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(attachmentImage)
            } else {
                attachmentGroup.visibility = View.GONE
                Glide.with(attachmentImage).clear(attachmentImage)
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
        getItem(position)?.let(holder::bind)
    }

    private companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}