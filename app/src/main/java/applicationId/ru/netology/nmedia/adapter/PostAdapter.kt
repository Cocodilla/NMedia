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

        init {
            binding.root.setOnClickListener {
                val post = getItem(bindingAdapterPositionSafe()) ?: return@setOnClickListener
                interactionListener.onPostClick(post)
            }

            binding.like.setOnClickListener {
                val post = getItem(bindingAdapterPositionSafe()) ?: return@setOnClickListener
                interactionListener.onLike(post)
            }

            binding.share.setOnClickListener {
                val post = getItem(bindingAdapterPositionSafe()) ?: return@setOnClickListener
                interactionListener.onShare(post)
            }

            binding.menu.setOnClickListener { v ->
                val post = getItem(bindingAdapterPositionSafe()) ?: return@setOnClickListener
                showMenu(post, v)
            }

            binding.videoGroup.setOnClickListener {
                val post = getItem(bindingAdapterPositionSafe()) ?: return@setOnClickListener
                if (!post.video.isNullOrEmpty()) interactionListener.onVideoPlay(post)
            }

            binding.playButton.setOnClickListener {
                val post = getItem(bindingAdapterPositionSafe()) ?: return@setOnClickListener
                if (!post.video.isNullOrEmpty()) interactionListener.onVideoPlay(post)
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

            // --- Video ---
            videoGroup.visibility = if (post.video.isNullOrEmpty()) View.GONE else View.VISIBLE

            // --- Avatar (circle) ---
            val avatarUrl = post.authorAvatar?.let { fileName ->
                "${BASE_URL}avatars/$fileName"
            }

            Glide.with(avatar)
                .load(avatarUrl)
                .timeout(10_000) // сервер тормозит
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_avatar_placeholder)
                .error(R.drawable.ic_avatar_placeholder)
                .into(avatar)

            // --- Attachment IMAGE ---
            val attachment = post.attachment
            if (attachment != null && attachment.type.equals("IMAGE", ignoreCase = true)) {
                attachmentGroup.visibility = View.VISIBLE

                val imageUrl = "${BASE_URL}images/${attachment.url}"
                Glide.with(attachmentImage)
                    .load(imageUrl)
                    .timeout(10_000) // сервер тормозит
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(attachmentImage)
            } else {
                attachmentGroup.visibility = View.GONE
                // чтобы не мигало старое изображение при реюзе холдера
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

        private fun bindingAdapterPositionSafe(): Int {
            val pos = bindingAdapterPosition
            return if (pos == RecyclerView.NO_POSITION) -1 else pos
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object {
        // baseUrl ДОЛЖЕН заканчиваться на /
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem == newItem
}
