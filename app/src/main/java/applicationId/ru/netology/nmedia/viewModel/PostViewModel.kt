 package applicationId.ru.netology.nmedia.viewModel

import android.util.Log
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

    init {
        loadPosts()
    }

    fun loadPosts() {
        repository.getAll(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(value)
            }

            override fun onError(e: Exception) {
                Log.e("PostViewModel", "loadPosts error", e)
            }
        })
    }

    fun like(id: Long) {
        val post = _data.value?.find { it.id == id } ?: return

        val cb = object : PostRepository.Callback<Post> {
            override fun onSuccess(value: Post) {
                val updated = _data.value.orEmpty().map { if (it.id == value.id) value else it }
                _data.postValue(updated)
            }

            override fun onError(e: Exception) {
                Log.e("PostViewModel", "like error", e)
            }
        }

        if (post.likedByMe) repository.unlikeById(id, cb)
        else repository.likeById(id, cb)
    }

    fun removeById(id: Long) {
        repository.removeById(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(value: Unit) {
                // можно локально убрать сразу
                _data.postValue(_data.value.orEmpty().filter { it.id != id })
                // и добрать актуальное с сервера (на случай сортировки/логики сервера)
                loadPosts()
            }

            override fun onError(e: Exception) {
                Log.e("PostViewModel", "remove error", e)
            }
        })
    }

    fun save(content: String) {
        repository.save(content, object : PostRepository.Callback<Post> {
            override fun onSuccess(value: Post) {
                // сервер может менять поля → лучше перезагрузить ленту
                loadPosts()
            }

            override fun onError(e: Exception) {
                Log.e("PostViewModel", "save error", e)
            }
        })
    }

    fun edit(id: Long, content: String) {
        repository.editById(id, content, object : PostRepository.Callback<Post> {
            override fun onSuccess(value: Post) {
                // сервер может менять поля → лучше перезагрузить ленту
                loadPosts()
            }

            override fun onError(e: Exception) {
                Log.e("PostViewModel", "edit error", e)
            }
        })
    }
}
