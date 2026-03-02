package applicationId.ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import applicationId.ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey
    val id: Long,
    val author: String,
    val content: String,
    val published: Long, // unix seconds
    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val video: String? = null,
    val authorAvatar: String? = null,
    val visible: Boolean = true,
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        content = content,
        publishedTimestamp = published,
        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        views = views,
        video = video,
        authorAvatar = authorAvatar,
        attachment = null // в базе не храним, при необходимости можно добавить
    )

    companion object {
        fun fromDto(dto: Post, visible: Boolean = true): PostEntity = PostEntity(
            id = dto.id,
            author = dto.author,
            content = dto.content,
            published = dto.publishedTimestamp,
            likedByMe = dto.likedByMe,
            likes = dto.likes,
            shares = dto.shares,
            views = dto.views,
            video = dto.video,
            authorAvatar = dto.authorAvatar,
            visible = visible
        )
    }
}