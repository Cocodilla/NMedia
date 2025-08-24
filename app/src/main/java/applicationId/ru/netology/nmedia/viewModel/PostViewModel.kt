package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.repository.PostRepository
import applicationId.ru.netology.nmedia.repository.PostRepositoryMemory

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryMemory()
    val data: LiveData<Post> = repository.get()

    fun like() = repository.like()
}


