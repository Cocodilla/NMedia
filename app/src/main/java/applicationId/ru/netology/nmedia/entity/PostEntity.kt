package applicationId.ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import applicationId.ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,

    // в БД всегда храним секунды unix time
    val published: Long,

    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val video: String? = null,

    // для New Posts
    val visible: Boolean = true,
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        content = content,

        published = published.toString(),

        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        views = views,
        video = video,
    )

    companion object {
        fun fromDto(dto: Post, visible: Boolean = true): PostEntity = PostEntity(
            id = dto.id,
            author = dto.author,
            content = dto.content,

            published = dto.published.toLongOrNull() ?: (System.currentTimeMillis() / 1000),

            likedByMe = dto.likedByMe,
            likes = dto.likes,
            shares = dto.shares,
            views = dto.views,
            video = dto.video,
            visible = visible,
        )
    }
}
