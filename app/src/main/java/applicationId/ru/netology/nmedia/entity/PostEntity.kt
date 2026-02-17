package applicationId.ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import applicationId.ru.netology.nmedia.dto.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val video: String?,
    val authorAvatar: String? = null,
    val attachmentUrl: String? = null,
    val attachmentDescription: String? = null,
    val attachmentType: String? = null,
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        views = views,
        video = video,
        authorAvatar = authorAvatar,
        attachment = if (attachmentUrl == null) null else Post.Attachment(
            url = attachmentUrl,
            description = attachmentDescription.orEmpty(),
            type = attachmentType.orEmpty()
        )
    )

    companion object {
        fun fromDto(dto: Post): PostEntity = PostEntity(
            id = dto.id,
            author = dto.author,
            content = dto.content,
            published = dto.published,
            likedByMe = dto.likedByMe,
            likes = dto.likes,
            shares = dto.shares,
            views = dto.views,
            video = dto.video,
            authorAvatar = dto.authorAvatar,
            attachmentUrl = dto.attachment?.url,
            attachmentDescription = dto.attachment?.description,
            attachmentType = dto.attachment?.type,
        )
    }
}
