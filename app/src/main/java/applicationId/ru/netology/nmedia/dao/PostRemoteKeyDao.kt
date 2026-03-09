package applicationId.ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import applicationId.ru.netology.nmedia.entity.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {

    @Query("SELECT * FROM PostRemoteKeyEntity WHERE type = :type LIMIT 1")
    suspend fun get(type: String): PostRemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: PostRemoteKeyEntity)

    @Query("DELETE FROM PostRemoteKeyEntity WHERE type = :type")
    suspend fun clear(type: String)
}