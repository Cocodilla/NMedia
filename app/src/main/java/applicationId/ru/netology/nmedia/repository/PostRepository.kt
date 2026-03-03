package applicationId.ru.netology.nmedia.repository

import androidx.paging.PagingData
import applicationId.ru.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun data(): Flow<PagingData<Post>>

    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(content: String)
    suspend fun edit(id: Long, content: String)
}