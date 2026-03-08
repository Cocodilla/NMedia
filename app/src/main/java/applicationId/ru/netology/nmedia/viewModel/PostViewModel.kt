package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.model.FeedItem
import applicationId.ru.netology.nmedia.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    val data: Flow<PagingData<FeedItem>> =
        repository.data()
            .map { pagingData ->
                pagingData
                    .map { post ->
                        FeedItem.PostItem(post)
                    }
                    .insertSeparators { before: FeedItem.PostItem?, after: FeedItem.PostItem? ->
                        if (after == null) return@insertSeparators null

                        val now = System.currentTimeMillis() / 1000
                        val diff = now - after.post.publishedTimestamp

                        val currentLabel = when {
                            diff < 24 * 60 * 60 -> "Сегодня"
                            diff < 48 * 60 * 60 -> "Вчера"
                            else -> "На прошлой неделе"
                        }

                        if (before == null) {
                            return@insertSeparators FeedItem.Separator(currentLabel)
                        }

                        val beforeDiff = now - before.post.publishedTimestamp
                        val beforeLabel = when {
                            beforeDiff < 24 * 60 * 60 -> "Сегодня"
                            beforeDiff < 48 * 60 * 60 -> "Вчера"
                            else -> "На прошлой неделе"
                        }

                        if (beforeLabel != currentLabel) {
                            FeedItem.Separator(currentLabel)
                        } else {
                            null
                        }
                    }
            }
            .cachedIn(viewModelScope)

    fun likeById(id: Long) {
        viewModelScope.launch {
            repository.likeById(id)
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun save(content: String) {
        viewModelScope.launch {
            repository.save(content)
        }
    }

    fun edit(id: Long, content: String) {
        viewModelScope.launch {
            repository.edit(id, content)
        }
    }
}