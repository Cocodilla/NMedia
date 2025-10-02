package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepository

// PostViewModel.kt
class PostViewModel(private val repository: PostRepository) : ViewModel() {
    private val _data = MutableLiveData<List<Post>>()
    val data: LiveData<List<Post>> = _data

    private val _editablePost = MutableLiveData<Post?>(null)
    val editablePost: LiveData<Post?> = _editablePost

    init {
        _data.value = repository.data.value ?: emptyList()
    }

    fun setPostForEditing(post: Post) {
        _editablePost.value = post
    }

    fun save(content: String) {
        val editablePost = _editablePost.value
        if (editablePost != null) {
            // Редактирование существующего поста
            val updatedPost = editablePost.copy(content = content)
            repository.save(updatedPost)
            _editablePost.value = null
        } else {
            // Создание нового поста
            val newPost = Post(
                id = 0, // 0 для нового поста
                author = "Me",
                content = content,
                published = "Now",
                likedByMe = false,
                likes = 0,
                shares = 0,
                views = 0
            )
            repository.save(newPost)
        }
        // Обновляем данные
        _data.value = repository.data.value
    }

    fun cancelEditing() {
        _editablePost.value = null
    }

    fun like(id: Long) {
        repository.like(id)
        _data.value = repository.data.value
    }

    fun share(id: Long) {
        repository.share(id)
        _data.value = repository.data.value
    }

    fun removeById(id: Long) {
        repository.removeById(id)
        // Если удаляем пост, который редактируется, выходим из режима редактирования
        if (_editablePost.value?.id == id) {
            _editablePost.value = null
        }
        _data.value = repository.data.value
    }
}