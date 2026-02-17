package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<List<Post>>

    suspend fun refresh()

    suspend fun save(content: String)
    suspend fun editById(id: Long, content: String)

    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
}
