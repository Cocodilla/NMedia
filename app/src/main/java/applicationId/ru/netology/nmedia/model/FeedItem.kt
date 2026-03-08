package applicationId.ru.netology.nmedia.model

import applicationId.ru.netology.nmedia.dto.Post

sealed class FeedItem {

    data class PostItem(
        val post: Post
    ) : FeedItem()

    data class Separator(
        val label: String
    ) : FeedItem()
}