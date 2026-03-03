package applicationId.ru.netology.nmedia.repository

import androidx.paging.*
import applicationId.ru.netology.nmedia.auth.AuthRepository
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dao.PostRemoteKeyDao
import applicationId.ru.netology.nmedia.db.AppDb
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.dto.PostsService
import applicationId.ru.netology.nmedia.dto.toApiForSave
import applicationId.ru.netology.nmedia.dto.toUi
import applicationId.ru.netology.nmedia.entity.PostEntity
import applicationId.ru.netology.nmedia.error.ApiError
import applicationId.ru.netology.nmedia.error.NetworkError
import applicationId.ru.netology.nmedia.error.UnknownError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class PostRepositoryImpl @Inject constructor(
    private val db: AppDb,
    private val dao: PostDao,
    private val keyDao: PostRemoteKeyDao,
    private val service: PostsService,
    private val authRepository: AuthRepository
) : PostRepository {
    override fun data(): Flow<PagingData<Post>> {
        return authRepository.authState
            .map { it.token }
            .distinctUntilChanged()
            .flatMapLatest {
                Pager(
                    config = PagingConfig(
                        pageSize = 10,
                        prefetchDistance = 2,
                        enablePlaceholders = false
                    ),
                    remoteMediator = PostRemoteMediator(
                        db = db,
                        postDao = dao,
                        keyDao = keyDao,
                        service = service
                    ),
                    pagingSourceFactory = { dao.pagingSource() }
                ).flow.map { pagingData ->
                    pagingData.map { entity -> entity.toDto() }
                }
            }
    }

    override suspend fun likeById(id: Long) {
        val current = dao.getById(id) ?: return
        val willLike = !current.likedByMe

        // optimistic update
        dao.toggleLikeLocal(id)

        try {
            val response =
                if (willLike) service.likeById(id)
                else service.unlikeById(id)

            if (!response.isSuccessful)
                throw ApiError(response.code(), response.message())

            val body = response.body()
                ?: throw ApiError(response.code(), response.message())

            dao.upsert(PostEntity.fromDto(body.toUi()))
        } catch (e: Exception) {
            // rollback
            dao.toggleLikeLocal(id)

            when (e) {
                is IOException -> throw NetworkError
                is ApiError -> throw e
                else -> throw UnknownError
            }
        }
    }

    override suspend fun removeById(id: Long) {
        val backup = dao.getById(id)
        dao.removeById(id)

        try {
            val response = service.removeById(id)
            if (!response.isSuccessful)
                throw ApiError(response.code(), response.message())
        } catch (e: Exception) {
            backup?.let { dao.upsert(it) }

            when (e) {
                is IOException -> throw NetworkError
                is ApiError -> throw e
                else -> throw UnknownError
            }
        }
    }

    override suspend fun save(content: String) {
        val post = Post(
            id = 0L,
            author = "Me",
            content = content,
            publishedTimestamp = 0L,
            likedByMe = false,
            likes = 0,
            shares = 0,
            views = 0,
            video = null,
            authorAvatar = null,
            attachment = null
        )

        try {
            val response = service.save(post.toApiForSave())
            if (!response.isSuccessful)
                throw ApiError(response.code(), response.message())

            val body = response.body()
                ?: throw ApiError(response.code(), response.message())

            dao.upsert(PostEntity.fromDto(body.toUi()))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun edit(id: Long, content: String) {
        val existing = dao.getById(id)?.toDto() ?: return
        val updated = existing.copy(content = content)

        try {
            val response = service.save(updated.toApiForSave())
            if (!response.isSuccessful)
                throw ApiError(response.code(), response.message())

            val body = response.body()
                ?: throw ApiError(response.code(), response.message())

            dao.upsert(PostEntity.fromDto(body.toUi()))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}