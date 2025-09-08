package applicationId.ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import applicationId.ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    fun like(id: Long)
    fun share(id: Long)
}