package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.*
import applicationId.ru.netology.nmedia.error.AppError
import applicationId.ru.netology.nmedia.error.UnknownError
import applicationId.ru.netology.nmedia.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    val data = repository.data.asLiveData()
    val newerCount = repository.newerCount.asLiveData()

    private val _state = MutableLiveData(FeedState())
    val state: LiveData<FeedState> = _state

    private var lastAction: (suspend () -> Unit)? = null

    init {
        loadPosts()
        startNewerPolling()
    }

    fun loadPosts() = runAction { repository.refresh() }
    fun likeById(id: Long) = runAction { repository.likeById(id) }
    fun removeById(id: Long) = runAction { repository.removeById(id) }
    fun save(content: String) = runAction { repository.save(content) }
    fun edit(id: Long, content: String) = runAction { repository.edit(id, content) }
    fun showNewer() = runAction { repository.showNewer() }

    fun retry() {
        val action = lastAction ?: return
        runAction(action)
    }


    private fun startNewerPolling() {
        flow {
            while (true) {
                delay(10_000) // интервал опроса
                emit(Unit)
            }
        }
            .onStart { emit(Unit) }
            .flowOn(Dispatchers.IO)
            .catch { e ->

            }
            .onEach {
                try {
                    repository.getNewer()
                } catch (e: Exception) {

                }
            }
            .launchIn(viewModelScope)
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
                _state.value = FeedState(error = UnknownError)
            }
        }
    }
}

data class FeedState(
    val loading: Boolean = false,
    val error: AppError? = null
)