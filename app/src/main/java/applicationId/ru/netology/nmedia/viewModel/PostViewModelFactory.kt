package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import applicationId.ru.netology.nmedia.repository.PostRepositoryMemory

class PostViewModelFactory(
    private val repository: PostRepositoryMemory
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверка если запрашиваемая ViewModel это PostViewModel
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}