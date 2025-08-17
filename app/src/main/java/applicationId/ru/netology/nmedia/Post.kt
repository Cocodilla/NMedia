package applicationId.ru.netology.nmedia

data class Post(
    val id: Int,
    val published: String,
    val content: String,
    val author: String,
    var likes: Int = 0,
    var likedByMe: Boolean = false,
    var shares: Int = 0,
    var views: Int = 0
)