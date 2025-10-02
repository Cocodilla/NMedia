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
import ru.netology.nmedia.util.NumberFormatter

class PostAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener,
    private val onRemoveListener: OnRemoveListener
) : ListAdapter<Post, PostAdapter.ViewHolder>(PostDiffCallback()) {

    // Объявление интерфейсов внутри адаптера
    interface OnLikeListener {
        fun onLike(post: Post)
    }

    interface OnShareListener {
        fun onShare(post: Post)
    }

    interface OnRemoveListener {
        fun removeById(post: Post)
    }

    interface OnLongClickListener {
        fun onLongClick(post: Post)
    }

    // Публичное свойство для установки извне
    var onLongClickListener: OnLongClickListener? = null

    // ViewHolder для элемента списка
    inner class ViewHolder(
        private val binding: CardPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentPost: Post? = null

        init {
            // Установка длинного нажатия на весь элемент
            itemView.setOnLongClickListener {
                currentPost?.let { post ->
                    this@PostAdapter.onLongClickListener?.onLongClick(post)
                    true
                } ?: false
            }

            binding.menu.setOnClickListener {
                currentPost?.let { post ->
                    showMenu(post, it)
                }
            }

            binding.like.setOnClickListener {
                currentPost?.let { post ->
                    onLikeListener.onLike(post)
                }
            }

            binding.share.setOnClickListener {
                currentPost?.let { post ->
                    onShareListener.onShare(post)
                }
            }
        }

        fun bind(post: Post) {
            currentPost = post
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likes.text = NumberFormatter.formatCount(post.likes)
                shares.text = NumberFormatter.formatCount(post.shares)
                views.text = NumberFormatter.formatCount(post.views)

                like.setImageResource(
                    if (post.likedByMe) R.drawable.love_like_heart_icon_196980 else R.drawable.heart
                )
            }
        }

        private fun showMenu(post: Post, view: View) {
            PopupMenu(view.context, view).apply {
                inflate(R.menu.menu_post)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onRemoveListener.removeById(post)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }

    // Создание нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

// Класс для сравнения элементов списка с помощью DiffUtil
class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}