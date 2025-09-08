package applicationId.ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicationId.ru.netology.nmedia.dto.Post

class PostRepositoryMemory : PostRepository {
    // Создаем начальный пост внутри класса
    private val initialPost = Post(
        id = 1,
        published = "Нетология. Университет интернет-профессий будущего",
        content = "Привет! Это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике, управлению. Мы растем сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остается с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия - помочь встать на путь роста и начать цепочку перемен.",
        author = "21 мая в 19.00",
        likes = 999999999,
        likedByMe = false,
        shares = 999,
        views = 999
    )

    private val data = MutableLiveData(initialPost)

    override fun like() {
        val currentPost = data.value ?: return
        val updatedPost = currentPost.copy(
            likedByMe = !currentPost.likedByMe,
            likes = if (currentPost.likedByMe) {
                currentPost.likes - 1
            } else {
                currentPost.likes + 1
            }
        )
        data.value = updatedPost
    }

    override fun share() {
        data.value?.let { currentPost ->
            val updatedPost = currentPost.copy(shares = currentPost.shares + 1)
            data.value = updatedPost
        }
    }
    override fun get(): LiveData<Post> = data
}