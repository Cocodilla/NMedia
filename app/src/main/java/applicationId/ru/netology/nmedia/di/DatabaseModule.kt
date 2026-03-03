package applicationId.ru.netology.nmedia.di

import android.content.Context
import androidx.room.Room
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
    fun provideAppDb(@ApplicationContext context: Context): AppDb =
        Room.databaseBuilder(context, AppDb::class.java, "app.db").build()

    @Provides
    @Singleton
    fun providePostDao(appDb: AppDb) = appDb.postDao()

    @Provides
    @Singleton
    fun providePostRemoteKeyDao(appDb: AppDb) = appDb.postRemoteKeyDao()
}