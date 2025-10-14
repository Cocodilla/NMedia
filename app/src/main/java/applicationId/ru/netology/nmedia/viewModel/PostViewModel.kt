package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepository

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val data: LiveData<List<Post>> = repository.data

    private val _editablePost = MutableLiveData<Post?>(null)
    val editablePost: LiveData<Post?> = _editablePost

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
            // Создание нового поста с уникальным ID
            val newPost = Post(
                id = generateId(),
                author = "Me",
                content = content,
                published = "Now",
                video = if (content.contains("rutube", ignoreCase = true)) "https://rutube.ru/video/6550a91e7e523f9503bed47e4c46d0cb" else null
            )
            repository.save(newPost)
        }
    }

    fun edit(postId: Long, content: String) {
        val currentPosts = data.value ?: return
        val existingPost = currentPosts.find { it.id == postId }
        existingPost?.let { post ->
            val updatedPost = post.copy(content = content)
            repository.save(updatedPost)
        }
    }

    fun cancelEditing() {
        _editablePost.value = null
    }

    fun like(id: Long) {
        repository.like(id)
    }

    fun share(id: Long) {
        repository.share(id)
    }

    fun removeById(id: Long) {
        repository.removeById(id)
        // Если удаляем пост, который редактируется, выходим из режима редактирования
        if (_editablePost.value?.id == id) {
            _editablePost.value = null
        }
    }

    private fun generateId(): Long {
        return System.currentTimeMillis()
    }
}