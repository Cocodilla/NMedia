package applicationId.ru.netology.nmedia.dto

data class PostApiModel(
    val id: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Long, // unix seconds
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null,
    val attachment: AttachmentApiModel? = null,
    val ownedByMe: Boolean = false,
)

// API -> UI
fun PostApiModel.toUi(): Post = Post(
    id = id,
    author = author,
    authorAvatar = authorAvatar,
    content = content,
    publishedTimestamp = published,
    likedByMe = likedByMe,
    likes = likes,
    shares = shares,
    views = views,
    video = video,
    attachment = attachment?.let {
        Attachment(
            url = it.url,
            description = it.description,
            type = Attachment.AttachmentType.valueOf(it.type.uppercase())
        )
    },
    ownedByMe = ownedByMe
)

// UI -> API (save/edit)
fun Post.toApiForSave(): PostApiModel = PostApiModel(
    id = id,
    author = author,
    authorAvatar = authorAvatar,
    content = content,
    published = if (id == 0L) 0L else publishedTimestamp,
    likedByMe = likedByMe,
    likes = likes,
    shares = shares,
    views = views,
    video = video,
    attachment = attachment?.let {
        AttachmentApiModel(
            url = it.url,
            description = it.description ?: "",
            type = it.type.name
        )
    }
)