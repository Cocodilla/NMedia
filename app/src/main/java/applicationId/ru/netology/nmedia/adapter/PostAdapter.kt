package applicationId.ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import applicationId.ru.netology.nmedia.databinding.CardPostBinding
import applicationId.ru.netology.nmedia.databinding.ItemSeparatorBinding
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.model.FeedItem

class PostAdapter(
    private val interactionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {

    interface OnInteractionListener {
        fun onLike(post: Post)
        fun onShare(post: Post)
        fun onRemove(post: Post)
        fun onEdit(post: Post)
        fun onVideoPlay(post: Post)
        fun onPostClick(post: Post)
    }

    override fun getItemViewType(position: Int): Int =
        when (peek(position)) {
            is FeedItem.PostItem -> TYPE_POST
            is FeedItem.Separator -> TYPE_SEPARATOR
            null -> TYPE_POST
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_POST -> {
                val binding = CardPostBinding.inflate(inflater, parent, false)
                PostViewHolder(binding, interactionListener)
            }

            TYPE_SEPARATOR -> {
                val binding = ItemSeparatorBinding.inflate(inflater, parent, false)
                SeparatorViewHolder(binding)
            }

            else -> error("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is FeedItem.PostItem -> (holder as PostViewHolder).bind(item.post)
            is FeedItem.Separator -> (holder as SeparatorViewHolder).bind(item)
            null -> Unit
        }
    }

    private companion object {
        const val TYPE_POST = 0
        const val TYPE_SEPARATOR = 1
    }
}