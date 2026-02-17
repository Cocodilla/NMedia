package applicationId.ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import applicationId.ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("""
        UPDATE posts SET
            likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END,
            likes = CASE WHEN likedByMe THEN MAX(likes - 1, 0) ELSE likes + 1 END
        WHERE id = :id
    """)
    suspend fun toggleLikeById(id: Long)

    @Query("DELETE FROM posts")
    suspend fun clear()
}
