package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepository




class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val data: LiveData<List<Post>> = repository.data

    fun like(id: Long) = repository.like(id)
    fun share(id: Long) = repository.share(id)
}