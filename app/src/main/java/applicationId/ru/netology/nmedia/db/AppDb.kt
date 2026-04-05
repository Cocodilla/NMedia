package applicationId.ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import applicationId.ru.netology.nmedia.dao.PointDao
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.entity.PointEntity
import applicationId.ru.netology.nmedia.entity.PostEntity

@Database(
    entities = [PostEntity::class, PointEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun pointDao(): PointDao
}