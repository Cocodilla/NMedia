package applicationId.ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile private var INSTANCE: AppDb? = null

        fun getInstance(context: Context): AppDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "app.db"
                ).build().also { INSTANCE = it }
            }
    }
}