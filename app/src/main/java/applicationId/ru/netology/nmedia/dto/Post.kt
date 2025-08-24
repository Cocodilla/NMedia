package applicationId.ru.netology.nmedia.dto

data class Post(
    val id: Int,
    val published: String,
    val content: String,
    val author: String,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    var shares: Int = 0,
    var views: Int = 0
)