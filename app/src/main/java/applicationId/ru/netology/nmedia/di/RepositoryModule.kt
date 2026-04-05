package applicationId.ru.netology.nmedia.di

import applicationId.ru.netology.nmedia.repository.PointRepository
import applicationId.ru.netology.nmedia.repository.PointRepositoryImpl
import applicationId.ru.netology.nmedia.repository.PostRepository
import applicationId.ru.netology.nmedia.repository.PostRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    fun bindPointRepository(impl: PointRepositoryImpl): PointRepository
}