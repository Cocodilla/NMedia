package applicationId.ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import applicationId.ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    // показываем только visible = 1
    @Query("SELECT * FROM PostEntity WHERE visible = 1 ORDER BY id DESC")
    fun getVisible(): Flow<List<PostEntity>>

    // сколько скрытых постов
    @Query("SELECT COUNT(*) FROM PostEntity WHERE visible = 0")
    fun countHidden(): Flow<Int>

    // сделать все скрытые — видимыми
    @Query("UPDATE PostEntity SET visible = 1 WHERE visible = 0")
    suspend fun showAllHidden()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun maxId(): Long?

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("""
        UPDATE PostEntity SET
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END,
        likes = CASE WHEN likedByMe THEN likes - 1 ELSE likes + 1 END
        WHERE id = :id
    """)
    suspend fun likeById(id: Long)
}
