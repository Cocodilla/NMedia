package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.dto.PostApiModel
import applicationId.ru.netology.nmedia.dto.toUi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {

    private val gson = Gson()

    // Dispatcher
    private val dispatcher = Dispatcher().apply {
        maxRequests = 64
        maxRequestsPerHost = 5
    }

    private val client = OkHttpClient.Builder()
        .dispatcher(dispatcher)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    init {
        println(
            "OkHttp Dispatcher configured: " +
                    "maxRequests=${dispatcher.maxRequests}, " +
                    "maxRequestsPerHost=${dispatcher.maxRequestsPerHost}"
        )
    }

    // ---------- GET ALL ----------
    override fun getAll(callback: PostRepository.Callback<List<Post>>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e) // ❗ network error
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(RuntimeException("Server error: ${it.code}"))
                        return
                    }

                    try {
                        val body = it.body?.string()
                            ?: throw RuntimeException("Empty body")

                        val type = object : TypeToken<List<PostApiModel>>() {}.type
                        val apiPosts: List<PostApiModel> = gson.fromJson(body, type)

                        callback.onSuccess(apiPosts.map { post -> post.toUi() })
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            }
        })
    }

    // ---------- LIKE ----------
    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .post(ByteArray(0).toRequestBody())
            .build()

        client.newCall(request).enqueue(postCallback(callback))
    }

    // ---------- UNLIKE ----------
    override fun unlikeById(id: Long, callback: PostRepository.Callback<Post>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .delete()
            .build()

        client.newCall(request).enqueue(postCallback(callback))
    }

    // ---------- SAVE ----------
    override fun save(content: String, callback: PostRepository.Callback<Post>) {
        val api = PostApiModel(
            id = 0L,
            author = "Me",
            content = content,
            published = System.currentTimeMillis() / 1000,
            likedByMe = false,
            likes = 0,
            shares = 0,
            views = 0,
            video = if (content.contains("rutube", ignoreCase = true))
                "https://rutube.ru/video/6550a91e7e523f9503bed47e4c46d0cb"
            else null
        )

        val body = gson.toJson(api)
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .post(body)
            .build()

        client.newCall(request).enqueue(postCallback(callback))
    }

    // ---------- EDIT ----------
    override fun editById(id: Long, content: String, callback: PostRepository.Callback<Post>) {
        val api = PostApiModel(
            id = id,
            author = "Me",
            content = content,
            published = System.currentTimeMillis() / 1000,
            likedByMe = false,
            likes = 0,
            shares = 0,
            views = 0,
            video = null
        )

        val body = gson.toJson(api)
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .post(body)
            .build()

        client.newCall(request).enqueue(postCallback(callback))
    }

    // ---------- REMOVE ----------
    override fun removeById(id: Long, callback: PostRepository.Callback<Unit>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(RuntimeException("Server error: ${it.code}"))
                        return
                    }
                    callback.onSuccess(Unit)
                }
            }
        })
    }

    // ---------- COMMON POST CALLBACK ----------
    private fun postCallback(callback: PostRepository.Callback<Post>): Callback =
        object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback.onError(RuntimeException("Server error: ${it.code}"))
                        return
                    }

                    try {
                        val body = it.body?.string()
                            ?: throw RuntimeException("Empty body")

                        val api = gson.fromJson(body, PostApiModel::class.java)
                        callback.onSuccess(api.toUi())
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            }
        }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
    }
}
