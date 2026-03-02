package applicationId.ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import applicationId.ru.netology.nmedia.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity WHERE visible = 1 ORDER BY id DESC")
    fun getVisible(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: Long): PostEntity?

    @Query("DELETE FROM PostEntity WHERE visible = 1")
    suspend fun deleteAllVisible()

    @Query("SELECT COALESCE(MAX(id), 0) FROM PostEntity")
    suspend fun maxId(): Long

    @Query("SELECT COUNT(*) FROM PostEntity WHERE visible = 0")
    fun countHidden(): Flow<Int>

    @Query("UPDATE PostEntity SET visible = 1 WHERE visible = 0")
    suspend fun showAll()

    @Upsert
    suspend fun upsert(posts: List<PostEntity>)

    @Upsert
    suspend fun upsert(post: PostEntity)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("""
        UPDATE PostEntity SET 
            likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
            likedByMe = NOT likedByMe
        WHERE id = :id
    """)
    suspend fun toggleLikeLocal(id: Long)
}