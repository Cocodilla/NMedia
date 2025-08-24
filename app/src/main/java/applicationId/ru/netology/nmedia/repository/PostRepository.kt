package applicationId.ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import applicationId.ru.netology.nmedia.dto.Post

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
}