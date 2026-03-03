package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.error.AppError
import applicationId.ru.netology.nmedia.error.UnknownError
import applicationId.ru.netology.nmedia.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedState(
    val loading: Boolean = false,
    val error: AppError? = null
)

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    // PagingData поток
    val data: Flow<PagingData<Post>> = repository.data().cachedIn(viewModelScope)

    private val _state = MutableStateFlow(FeedState())
    val state = _state.asStateFlow()

    private fun runAction(action: suspend () -> Unit) {
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

    fun likeById(id: Long) = runAction { repository.likeById(id) }
    fun removeById(id: Long) = runAction { repository.removeById(id) }
    fun save(content: String) = runAction { repository.save(content) }
    fun edit(id: Long, content: String) = runAction { repository.edit(id, content) }
}