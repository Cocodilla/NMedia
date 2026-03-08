package applicationId.ru.netology.nmedia.adapter

import androidx.recyclerview.widget.DiffUtil
import applicationId.ru.netology.nmedia.model.FeedItem

class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {

    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean =
        oldItem == newItem
}