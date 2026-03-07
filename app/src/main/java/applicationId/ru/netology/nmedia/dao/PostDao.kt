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

    /**
     * Минимальный id (самый старый пост)
     */
    @Query("SELECT MIN(id) FROM PostEntity")
    suspend fun minId(): Long?

    /**
     * Максимальный id (самый новый пост)
     */
    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun maxId(): Long?

    @Query("SELECT * FROM PostEntity WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<PostEntity?>

    @Query("SELECT * FROM PostEntity WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PostEntity?

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
            likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
            likedByMe = NOT likedByMe
        WHERE id = :id
        """
    )
    suspend fun toggleLikeLocal(id: Long)
}