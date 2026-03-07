package applicationId.ru.netology.nmedia.repository

import androidx.paging.*
import androidx.room.withTransaction
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dao.PostRemoteKeyDao
import applicationId.ru.netology.nmedia.db.AppDb
import applicationId.ru.netology.nmedia.dto.PostsService
import applicationId.ru.netology.nmedia.dto.toUi
import applicationId.ru.netology.nmedia.entity.PostEntity
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

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

        return try {

            val pageSize = state.config.pageSize

            when (loadType) {

                LoadType.REFRESH -> {

                    val maxId = postDao.maxId()

                    val response = if (maxId == null) {
                        // БД пустая
                        service.getLatest(pageSize)
                    } else {
                        // получаем только новые посты
                        service.getNewer(maxId, pageSize)
                    }

                    if (!response.isSuccessful)
                        throw ApiError(response.code(), response.message())

                    val body = response.body()
                        ?: throw ApiError(response.code(), response.message())

                    db.withTransaction {
                        postDao.upsert(
                            body.map { PostEntity.fromDto(it.toUi()) }
                        )
                    }

                    MediatorResult.Success(
                        endOfPaginationReached = body.isEmpty()
                    )
                }

                /**
                 * PREPEND отключён
                 */
                LoadType.PREPEND -> {
                    MediatorResult.Success(endOfPaginationReached = true)
                }

                /**
                 * APPEND — загрузка старых постов
                 */
                LoadType.APPEND -> {

                    val minId = postDao.minId()
                        ?: return MediatorResult.Success(true)

                    val response = service.getBefore(minId, pageSize)

                    if (!response.isSuccessful)
                        throw ApiError(response.code(), response.message())

                    val body = response.body()
                        ?: throw ApiError(response.code(), response.message())

                    db.withTransaction {
                        postDao.upsert(
                            body.map { PostEntity.fromDto(it.toUi()) }
                        )
                    }

                    MediatorResult.Success(
                        endOfPaginationReached = body.isEmpty()
                    )
                }
            }

        } catch (e: IOException) {
            MediatorResult.Error(NetworkError)
        } catch (e: ApiError) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(UnknownError)
        }
    }
}