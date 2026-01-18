package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepository
import applicationId.ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryImpl()

    val data: LiveData<List<Post>> = repository.data

    private val _editablePost = MutableLiveData<Post?>(null)
    val editablePost: LiveData<Post?> = _editablePost

    fun loadPosts() {
        (repository as? PostRepositoryImpl)?.refresh()
    }

    fun like(id: Long) {
        repository.like(id)
    }

    fun share(id: Long) {
        repository.share(id)
    }

    fun removeById(id: Long) {
        repository.removeById(id)
        if (_editablePost.value?.id == id) {
            _editablePost.value = null
        }
    }

    fun save(content: String) {
        val post = Post(
            id = System.currentTimeMillis(),
            author = "Me",
            content = content,
            published = "Now",
            likedByMe = false,
            likes = 0,
            shares = 0,
            views = 0,
            video = null
        )
        repository.save(post)
    }
    fun edit(id: Long, content: String) {
        val current = data.value.orEmpty()
        val oldPost = current.find { it.id == id } ?: return
        repository.save(oldPost.copy(content = content))
    }

    fun setPostForEditing(post: Post) {
        _editablePost.value = post
    }

    fun cancelEditing() {
        _editablePost.value = null
    }
}
