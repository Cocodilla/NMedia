package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepository
import applicationId.ru.netology.nmedia.repository.PostRepositoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryImpl()

    private val _data = MutableLiveData<List<Post>>(emptyList())
    val data: LiveData<List<Post>> = _data

    //  текст ошибки для UI
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadPosts()
    }

    fun loadPosts() {
        repository.getAll(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(value)
                _error.postValue(null)
            }

            override fun onError(e: Exception) {
                _error.postValue(e.message ?: "Ошибка загрузки")
                e.printStackTrace()
            }
        })
    }

    fun like(id: Long) {
        val post = _data.value?.find { it.id == id } ?: return

        val cb = object : PostRepository.Callback<Post> {
            override fun onSuccess(value: Post) {
                val updated = _data.value.orEmpty().map { if (it.id == value.id) value else it }
                _data.postValue(updated)
                _error.postValue(null)
            }

            override fun onError(e: Exception) {
                _error.postValue(e.message ?: "Ошибка лайка")
                e.printStackTrace()
            }
        }

        if (post.likedByMe) repository.unlikeById(id, cb)
        else repository.likeById(id, cb)
    }

    fun removeById(id: Long) {
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(_data.value.orEmpty().filter { it.id != id })
                _error.postValue(null)
            }

            override fun onError(e: Exception) {
                _error.postValue(e.message ?: "Ошибка удаления")
                e.printStackTrace()
            }
        })
    }

    fun save(content: String) {
        repository.save(content, object : PostRepository.Callback<Post> {
            override fun onSuccess(value: Post) {
                _data.postValue(listOf(value) + _data.value.orEmpty())
                _error.postValue(null)
            }

            override fun onError(e: Exception) {
                _error.postValue(e.message ?: "Ошибка сохранения")
                e.printStackTrace()
            }
        })
    }

    fun edit(id: Long, content: String) {
        repository.editById(id, content, object : PostRepository.Callback<Post> {
            override fun onSuccess(value: Post) {
                val updated = _data.value.orEmpty().map { if (it.id == value.id) value else it }
                _data.postValue(updated)
                _error.postValue(null)
            }

            override fun onError(e: Exception) {
                _error.postValue(e.message ?: "Ошибка редактирования")
                e.printStackTrace()
            }
        })
    }

    // не показывать одну и ту же ошибку бесконечно при повороте экрана
    fun clearError() {
        _error.value = null
    }
}
