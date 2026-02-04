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
import applicationId.ru.netology.nmedia.dto.AttachmentType
import applicationId.ru.netology.nmedia.dto.Post
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

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

        fun bind(post: Post) = with(binding) {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            like.isChecked = post.likedByMe
            like.text = post.likes.toString()
            share.text = post.shares.toString()
            views.text = post.views.toString()

            // --- Avatar ---
            val avatarUrl = post.authorAvatar?.let { fileName ->
                "$BASE_URL/avatars/$fileName"
            }

            Glide.with(avatar)
                .load(avatarUrl)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .timeout(10_000)
                .into(avatar)

            // --- Video block ---
            if (post.video.isNullOrEmpty()) {
                videoGroup.visibility = View.GONE
            } else {
                videoGroup.visibility = View.VISIBLE
            }

            // --- Attachment (IMAGE) ---
            val att = post.attachment
            if (att == null) {
                attachmentGroup.visibility = View.GONE
            } else {
                attachmentGroup.visibility = View.VISIBLE

                when (att.type) {
                    AttachmentType.IMAGE -> {
                        val imageUrl = "$BASE_URL/images/${att.url}"

                        Glide.with(attachmentImage)
                            .load(imageUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_image_placeholder)
                            .timeout(10_000)
                            .into(attachmentImage)

                    }
                }
            }

            // --- Clicks ---
            root.setOnClickListener { interactionListener.onPostClick(post) }
            like.setOnClickListener { interactionListener.onLike(post) }
            share.setOnClickListener { interactionListener.onShare(post) }

            menu.setOnClickListener { view ->
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

            videoGroup.setOnClickListener {
                if (!post.video.isNullOrEmpty()) interactionListener.onVideoPlay(post)
            }
            playButton.setOnClickListener {
                if (!post.video.isNullOrEmpty()) interactionListener.onVideoPlay(post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem == newItem
}
