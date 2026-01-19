package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel : ViewModel() {

    private val repository = PostRepositoryImpl()

    // данные репозитория
    val data: LiveData<List<Post>> = repository.data

    fun loadPosts() {
        repository.refresh()
    }

    fun like(id: Long) {
        repository.like(id)
    }

    fun removeById(id: Long) {
        repository.removeById(id)
    }

    fun share(id: Long) {
        repository.share(id)
    }

    fun save(content: String) {
        val post = Post(
            id = 0L,            // новый пост для сервера
            author = "Me",
            content = content,
            published = "",      // UI-строка, серверу уйдёт Long внутри API-модели
        )
        repository.save(post)
    }

    fun edit(id: Long, content: String) {
        val current = data.value.orEmpty()
        val old = current.find { it.id == id } ?: return
        repository.save(old.copy(content = content))
    }
}
