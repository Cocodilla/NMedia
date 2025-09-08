package applicationId.ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import applicationId.ru.netology.nmedia.R
import applicationId.ru.netology.nmedia.databinding.CardPostBinding
import applicationId.ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.NumberFormatter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter


// Интерфейсы для обработки событий
interface OnLikeListener {
    fun onLike(post: Post)
}

interface OnShareListener {
    fun onShare(post: Post)
}

class PostAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : ListAdapter<Post, PostAdapter.ViewHolder>(PostDiffCallback()) {
    interface OnLikeListener {
        fun onLike(post: Post)
    }

    interface OnShareListener {
        fun onShare(post: Post)
    }
    // ViewHolder для элемента списка
    class ViewHolder(
        private val binding: CardPostBinding,
        private val onLikeListener: OnLikeListener,
        private val onShareListener: OnShareListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = post.published
                content.text = post.content
                likes.text = NumberFormatter.formatCount(post.likes)
                shares.text = NumberFormatter.formatCount(post.shares)
                views.text = NumberFormatter.formatCount(post.views)

                // Установка изображения для лайка в зависимости от состояния
                like.setImageResource(
                    if (post.likedByMe) R.drawable.love_like_heart_icon_196980 else R.drawable.heart
                )

                // Обработчики кликов
                like.setOnClickListener { onLikeListener.onLike(post) }
                share.setOnClickListener { onShareListener.onShare(post) }
            }
        }
    }

    // Создание нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onLikeListener, onShareListener)
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