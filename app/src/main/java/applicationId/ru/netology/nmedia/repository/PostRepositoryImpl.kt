package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.dto.PostApiModel
import applicationId.ru.netology.nmedia.dto.PostsService
import applicationId.ru.netology.nmedia.dto.toApiForSave
import applicationId.ru.netology.nmedia.dto.toUi
import applicationId.ru.netology.nmedia.entity.PostEntity
import applicationId.ru.netology.nmedia.error.ApiError
import applicationId.ru.netology.nmedia.error.NetworkError
import applicationId.ru.netology.nmedia.error.UnknownError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val service: PostsService
) : PostRepository {

    override val data: Flow<List<Post>> =
        dao.getVisible().map { entities -> entities.map(PostEntity::toDto) }

    override val newerCount: Flow<Int> = dao.countHidden()

    override suspend fun refresh() {
        try {
            val response = service.getAll()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            dao.deleteAllVisible()
            val entities = body.map { PostEntity.fromDto(it.toUi(), visible = true) }
            dao.upsert(entities)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        val current = dao.getPostById(id) ?: return
        val willLike = !current.likedByMe

        dao.toggleLikeLocal(id)

        try {
            val response = if (willLike) service.likeById(id) else service.unlikeById(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.upsert(PostEntity.fromDto(body.toUi(), visible = true))
        } catch (e: Exception) {
            dao.toggleLikeLocal(id)
            when (e) {
                is IOException -> throw NetworkError
                is ApiError -> throw e
                else -> throw UnknownError
            }
        }
    }

    override suspend fun removeById(id: Long) {
        val backup = dao.getPostById(id)
        dao.removeById(id)

        try {
            val response = service.removeById(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
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
        val apiModel = post.toApiForSave()
        try {
            val response = service.save(apiModel)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.upsert(PostEntity.fromDto(body.toUi(), visible = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun edit(id: Long, content: String) {
        val existing = dao.getPostById(id)?.toDto() ?: return
        val updated = existing.copy(content = content)
        val apiModel = updated.toApiForSave()
        try {
            val response = service.save(apiModel)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.upsert(PostEntity.fromDto(body.toUi(), visible = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getNewer() {
        try {
            val lastId = dao.maxId()
            val response = service.getNewer(lastId)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (body.isEmpty()) return
            val entities = body.map { PostEntity.fromDto(it.toUi(), visible = false) }
            dao.upsert(entities)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun showNewer() {
        dao.showAll()
    }
}