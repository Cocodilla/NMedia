package applicationId.ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.entity.PostEntity

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}