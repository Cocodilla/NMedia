package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dto.PostsApi
import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.dto.PostApiModel
import applicationId.ru.netology.nmedia.dto.toUi
import applicationId.ru.netology.nmedia.entity.PostEntity
import applicationId.ru.netology.nmedia.error.ApiError
import applicationId.ru.netology.nmedia.error.NetworkError
import applicationId.ru.netology.nmedia.error.UnknownError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class PostRepositoryImpl(
    private val dao: PostDao
) : PostRepository {

    override val data: Flow<List<Post>> =
        dao.getAll().map { list -> list.map(PostEntity::toDto) }

    override suspend fun refresh() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            dao.insert(body.map { PostEntity.fromDto(it.toUi()) })
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(content: String) {
        try {
            val api = PostApiModel(
                id = 0L,
                author = "Me",
                content = content,
                published = System.currentTimeMillis() / 1000
            )

            val response = PostsApi.service.save(api)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            dao.insert(PostEntity.fromDto(body.toUi()))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun editById(id: Long, content: String) {
        try {
            val api = PostApiModel(
                id = id,
                author = "Me",
                content = content,
                published = System.currentTimeMillis() / 1000
            )

            val response = PostsApi.service.save(api)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            dao.insert(PostEntity.fromDto(body.toUi()))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        // 1) оптимистично меняем локально
        dao.toggleLikeById(id)

        // 2) пробуем на сервер
        try {
            val post = dao.getById(id) ?: return
            val response =
                if (post.likedByMe) PostsApi.service.likeById(id) else PostsApi.service.unlikeById(id)

            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            // 3) приводим локально к серверной истине
            dao.insert(PostEntity.fromDto(body.toUi()))
        } catch (e: Exception) {
            // rollback (вернём как было)
            dao.toggleLikeById(id)

            if (e is IOException) throw NetworkError
            if (e is ApiError) throw e
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        // 1) запомним удаляемый пост и удалим локально
        val backup = dao.getById(id)
        dao.removeById(id)

        // 2) удаляем на сервере
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        } catch (e: Exception) {
            // rollback
            if (backup != null) dao.insert(backup)

            if (e is IOException) throw NetworkError
            if (e is ApiError) throw e
            throw UnknownError
        }
    }
}
