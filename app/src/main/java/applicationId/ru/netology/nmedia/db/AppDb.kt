package applicationId.ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dao.PostRemoteKeyDao
import applicationId.ru.netology.nmedia.entity.PostEntity
import applicationId.ru.netology.nmedia.entity.PostRemoteKeyEntity

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}