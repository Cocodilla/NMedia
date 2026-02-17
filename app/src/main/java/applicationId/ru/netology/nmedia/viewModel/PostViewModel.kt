package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.*
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.error.AppError
import applicationId.ru.netology.nmedia.repository.PostRepository
import kotlinx.coroutines.launch

data class FeedState(
    val loading: Boolean = false,
    val error: AppError? = null
)

class PostViewModel(
    private val repository: PostRepository
) : ViewModel() {

    val data: LiveData<List<Post>> = repository.data.asLiveData()

    private val _state = MutableLiveData(FeedState())
    val state: LiveData<FeedState> = _state

    private var lastAction: (suspend () -> Unit)? = null

    init {
        loadPosts()
    }

    fun loadPosts() = runAction({ repository.refresh() })

    fun save(content: String) = runAction({ repository.save(content) })

    fun edit(id: Long, content: String) = runAction({ repository.editById(id, content) })

    fun likeById(id: Long) = runAction({ repository.likeById(id) })

    fun removeById(id: Long) = runAction({ repository.removeById(id) })

    fun retry() {
        val action = lastAction ?: return
        runAction(action)
    }


    private fun runAction(action: suspend () -> Unit) {
        lastAction = action
        viewModelScope.launch {
            _state.value = FeedState(loading = true)
            try {
                action()
                _state.value = FeedState()
            } catch (e: AppError) {
                _state.value = FeedState(error = e)
            } catch (e: Exception) {
                _state.value = FeedState(error = null)
            }
        }
    }
}
