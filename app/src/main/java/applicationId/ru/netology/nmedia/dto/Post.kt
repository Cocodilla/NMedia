package applicationId.ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null,

    val authorAvatar: String? = null,
    val attachment: Attachment? = null
) : Parcelable {
    @Parcelize
    data class Attachment(
        val url: String,
        val description: String,
        val type: String
    ) : Parcelable
}
