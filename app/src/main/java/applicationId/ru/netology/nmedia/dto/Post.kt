package applicationId.ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Long,
    val published: String,
    val content: String,
    val author: String,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    var shares: Int = 0,
    var views: Int = 0,
    val video: String? = null
) : Parcelable