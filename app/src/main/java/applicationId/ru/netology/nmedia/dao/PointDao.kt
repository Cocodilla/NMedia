package applicationId.ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import applicationId.ru.netology.nmedia.entity.PointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointDao {

    @Query("SELECT * FROM points ORDER BY id DESC")
    fun getAll(): Flow<List<PointEntity>>

    @Query("SELECT * FROM points WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PointEntity?

    @Upsert
    suspend fun upsert(point: PointEntity)

    @Query("DELETE FROM points WHERE id = :id")
    suspend fun removeById(id: Long)
}