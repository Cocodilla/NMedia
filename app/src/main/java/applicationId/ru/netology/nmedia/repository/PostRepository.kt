package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<List<Post>>
    val newerCount: Flow<Int>

    suspend fun refresh()
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(content: String)
    suspend fun edit(id: Long, content: String)
    suspend fun getNewer()
    suspend fun showNewer()
}