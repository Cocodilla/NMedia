package applicationId.ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import applicationId.ru.netology.nmedia.databinding.ItemSeparatorBinding
import applicationId.ru.netology.nmedia.model.FeedItem

class SeparatorViewHolder(
    private val binding: ItemSeparatorBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: FeedItem.Separator) {
        binding.text.text = item.label
    }
}