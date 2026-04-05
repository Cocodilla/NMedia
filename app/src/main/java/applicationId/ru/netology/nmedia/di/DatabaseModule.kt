package applicationId.ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
import applicationId.ru.netology.nmedia.dao.PointDao
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(
        @ApplicationContext context: Context,
    ): AppDb = Room.databaseBuilder(
        context,
        AppDb::class.java,
        "app.db"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePointDao(db: AppDb): PointDao = db.pointDao()
}