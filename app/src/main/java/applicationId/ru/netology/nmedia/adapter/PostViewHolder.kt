package applicationId.ru.netology.nmedia.adapter

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.CardPostBinding
import applicationId.ru.netology.nmedia.dto.Attachment
import applicationId.ru.netology.nmedia.dto.Post
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PostViewHolder(
    private val binding: CardPostBinding,
    private val interactionListener: PostAdapter.OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) = with(binding) {
        author.text = post.author
        published.text = post.published
        content.text = post.content

        like.isChecked = post.likedByMe
        like.text = post.likes.toString()
        share.text = post.shares.toString()
        views.text = post.views.toString()

        root.setOnClickListener {
            interactionListener.onPostClick(post)
        }

        like.setOnClickListener {
            interactionListener.onLike(post)
        }

        share.setOnClickListener {
            interactionListener.onShare(post)
        }

        menu.visibility = if (post.ownedByMe) View.VISIBLE else View.GONE
        menu.setOnClickListener { view ->
            showMenu(post, view)
        }

        videoGroup.visibility = if (post.video.isNullOrEmpty()) View.GONE else View.VISIBLE
        if (!post.video.isNullOrEmpty()) {
            videoGroup.setOnClickListener {
                interactionListener.onVideoPlay(post)
            }
            playButton.setOnClickListener {
                interactionListener.onVideoPlay(post)
            }
        } else {
            videoGroup.setOnClickListener(null)
            playButton.setOnClickListener(null)
        }

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

    private companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }
}