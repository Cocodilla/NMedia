package applicationId.ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dao.PostRemoteKeyDao
import applicationId.ru.netology.nmedia.db.AppDb
import applicationId.ru.netology.nmedia.dto.PostsService
import applicationId.ru.netology.nmedia.dto.toUi
import applicationId.ru.netology.nmedia.entity.PostEntity
import applicationId.ru.netology.nmedia.entity.PostRemoteKeyEntity
import applicationId.ru.netology.nmedia.error.ApiError
import applicationId.ru.netology.nmedia.error.NetworkError
import applicationId.ru.netology.nmedia.error.UnknownError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val db: AppDb,
    private val postDao: PostDao,
    private val keyDao: PostRemoteKeyDao,
    private val service: PostsService
) : RemoteMediator<Int, PostEntity>() {

    private companion object {
        private const val KEY = "POSTS"
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val pageSize = state.config.pageSize

            val response = when (loadType) {
                LoadType.REFRESH -> {
                    service.getLatest(pageSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    // Для "вниз" берем самый старый id из базы
                    val minId = postDao.minId() ?: return MediatorResult.Success(true)
                    service.getBefore(minId, pageSize)
                }
            }

            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val entities = body.map { PostEntity.fromDto(it.toUi()) }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    keyDao.clear(KEY)
                    postDao.clear()
                }
                val newMin = entities.minOfOrNull { it.id }
                if (newMin != null) {
                    keyDao.insert(PostRemoteKeyEntity(KEY, newMin))
                }

                postDao.upsert(entities)
            }

            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(NetworkError)
        } catch (e: ApiError) {
            return MediatorResult.Error(e)
        } catch (e: Exception) {
            return MediatorResult.Error(UnknownError)
        }
    }
}