package applicationId.ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import applicationId.ru.netology.nmedia.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Upsert
    suspend fun upsert(posts: List<PostEntity>)

    @Upsert
    suspend fun upsert(post: PostEntity)

    @Query("DELETE FROM PostEntity")
    suspend fun clear()

    @Query("SELECT MIN(id) FROM PostEntity")
    suspend fun minId(): Long?

    @Query("SELECT * FROM PostEntity WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PostEntity?

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM PostEntity WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<PostEntity?>

    @Query("""
        UPDATE PostEntity SET 
            likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
            likedByMe = NOT likedByMe
        WHERE id = :id
    """)
    suspend fun toggleLikeLocal(id: Long)
}