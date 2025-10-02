package applicationId.ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicationId.ru.netology.nmedia.dto.Post

class PostRepositoryMemory : PostRepository {
    private var posts = listOf(
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий",
            content = "Привет, это новая Нетология!",
            published = "21 мая в 18:36",
            likedByMe = false,
            likes = 999,
            shares = 150,
            views = 2000
        ),
        Post(
            id = 2,
            author = "Иван Иванов",
            content = "Сегодня прекрасный день для изучения Android разработки!",
            published = "22 мая в 10:15",
            likedByMe = true,
            likes = 45,
            shares = 5,
            views = 120
        ),
        Post(
            id = 3,
            author = "Мария Петрова",
            content = "Поделюсь своими успехами в изучении Kotlin. Очень нравится язык!",
            published = "22 мая в 14:30",
            likedByMe = false,
            likes = 32,
            shares = 2,
            views = 89
        )
    )

    private val _data = MutableLiveData(posts)

    override val data: LiveData<List<Post>>
        get() = _data

    override fun like(id: Long) {
        _data.value = _data.value?.map { post ->
            if (post.id == id) {
                post.copy(
                    likedByMe = !post.likedByMe,
                    likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
                )
            } else {
                post
            }
        }
    }

    override fun share(id: Long) {
        _data.value = _data.value?.map { post ->
            if (post.id == id) {
                post.copy(shares = post.shares + 1)
            } else {
                post
            }
        }
    }

    override fun removeById(id: Long) {
        _data.value = _data.value?.filter { it.id != id }
    }

    override fun save(post: Post) {
        val currentList = _data.value ?: emptyList()
        // Обновляем существующий пост или добавляем новый
        _data.value = if (currentList.any { it.id == post.id }) {
            currentList.map { if (it.id == post.id) post else it }
        } else {
            listOf(post) + currentList
        }
    }
}