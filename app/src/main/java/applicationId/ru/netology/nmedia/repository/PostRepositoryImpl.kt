package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.dto.PostApiModel
import applicationId.ru.netology.nmedia.dto.PostsApi
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
    private val api: PostsApi
) : PostRepository {

    override val data: Flow<List<Post>> =
        dao.getVisible().map { list -> list.map(PostEntity::toDto) }

    override val newerCount: Flow<Int> = dao.countHidden()

    override suspend fun refresh() {
        try {
            val response = api.service.getAll()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            dao.deleteAllVisible()
            val entities = body.map { apiModel: PostApiModel ->
                PostEntity.fromDto(apiModel.toUi(), visible = true)
            }
            dao.upsert(entities)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        val currentPost = dao.getPostById(id) ?: return
        val newLikedByMe = !currentPost.likedByMe

        dao.toggleLikeLocal(id)

        try {
            val response = if (newLikedByMe) {
                api.service.likeById(id)
            } else {
                api.service.unlikeById(id)
            }
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
        val post = dao.getPostById(id)
        dao.removeById(id)

        try {
            val response = api.service.removeById(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        } catch (e: Exception) {
            post?.let { dao.upsert(it) }
            when (e) {
                is IOException -> throw NetworkError
                is ApiError -> throw e
                else -> throw UnknownError
            }
        }
    }

    override suspend fun save(content: String) {
        val post = PostApiModel(
            id = 0L,
            author = "",
            authorAvatar = null,
            content = content,
            published = 0L,
            likedByMe = false,
            likes = 0,
            shares = 0,
            views = 0,
            video = null,
            attachment = null
        )
        try {
            val response = api.service.create(post)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.upsert(PostEntity.fromDto(body.toUi(), visible = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun edit(id: Long, content: String) {
        val existing = dao.getPostById(id)?.toDto() ?: return
        val updatedPost = existing.copy(content = content)
        val apiModel = updatedPost.toApiForSave()

        try {
            val response = api.service.update(id, apiModel)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.upsert(PostEntity.fromDto(body.toUi(), visible = true))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getNewer() {
        try {
            val lastId = dao.maxId()
            val response = api.service.getNewer(lastId)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (body.isEmpty()) return

            val entities = body.map { apiModel: PostApiModel ->
                PostEntity.fromDto(apiModel.toUi(), visible = false)
            }
            dao.upsert(entities)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun showNewer() {
        dao.showAll()
    }
}