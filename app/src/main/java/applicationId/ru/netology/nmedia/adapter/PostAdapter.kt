package applicationId.ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
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
import ru.netology.nmedia.util.NumberFormatter

class PostAdapter(
    private val interactionListener: OnInteractionListener
) : ListAdapter<Post, PostAdapter.ViewHolder>(PostDiffCallback()) {

    interface OnInteractionListener {
        fun onLike(post: Post)
        fun onShare(post: Post)
        fun onRemove(post: Post)
        fun onEdit(post: Post)
        fun onVideoPlay(post: Post)
    }

    inner class ViewHolder(
        private val binding: CardPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentPost: Post? = null

        init {
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

            // Обработчик клика на видео блок
            binding.videoGroup.setOnClickListener {
                currentPost?.let { post ->
                    if (!post.video.isNullOrEmpty()) {
                        interactionListener.onVideoPlay(post)
                    }
                }
            }

            // Обработчик клика на кнопку play
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
                views.text = NumberFormatter.formatCount(post.views)
                like.isChecked = post.likedByMe
                like.text = NumberFormatter.formatCount(post.likes)
                share.text = NumberFormatter.formatCount(post.shares)

                // Показываем или скрываем блок с видео
                if (post.video.isNullOrEmpty()) {
                    videoGroup.visibility = View.GONE
                } else {
                    videoGroup.visibility = View.VISIBLE
                }

                // Обработчик нажатия на кнопку Play
                playButton.setOnClickListener {
                    playVideo(post.video)
                }

                // Также можно сделать кликабельным весь видео-блок
                videoGroup.setOnClickListener {
                    playVideo(post.video)
                }
            }
        }

        private fun playVideo(videoUrl: String?) {
            videoUrl?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                val chooser = Intent.createChooser(intent, "Play video with")
                if (intent.resolveActivity(binding.root.context.packageManager) != null) {
                    binding.root.context.startActivity(chooser)
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