package applicationId.ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.dto.PostApiModel
import applicationId.ru.netology.nmedia.dto.toUi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient()
    private val gson = Gson()

    private val _data = MutableLiveData<List<Post>>(emptyList())
    override val data: LiveData<List<Post>> = _data
    private val publishedById = mutableMapOf<Long, Long>()

    init {
        refresh()
    }

    fun refresh() {
        thread {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL/api/posts")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw RuntimeException("Error: ${response.code}")

                    val body = response.body?.string() ?: throw RuntimeException("Empty body")
                    val type = object : TypeToken<List<PostApiModel>>() {}.type
                    val apiPosts: List<PostApiModel> = gson.fromJson(body, type)
                    publishedById.clear()
                    apiPosts.forEach { publishedById[it.id] = it.published }

                    _data.postValue(apiPosts.map { it.toUi() })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun like(id: Long) {
        val current = _data.value.orEmpty()
        val target = current.find { it.id == id } ?: return

        thread {
            try {
                val request = if (target.likedByMe) {
                    Request.Builder()
                        .url("$BASE_URL/api/posts/$id/likes")
                        .delete()
                        .build()
                } else {
                    Request.Builder()
                        .url("$BASE_URL/api/posts/$id/likes")
                        .post(ByteArray(0).toRequestBody())
                        .build()
                }

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw RuntimeException("Error: ${response.code}")

                    val body = response.body?.string() ?: throw RuntimeException("Empty body")
                    val updatedApi = gson.fromJson(body, PostApiModel::class.java)

                    //  сервер вернул обновлённый пост — обновляем published map
                    publishedById[updatedApi.id] = updatedApi.published

                    val updatedUi = updatedApi.toUi()
                    val updatedList = _data.value.orEmpty().map { p ->
                        if (p.id == updatedUi.id) updatedUi else p
                    }
                    _data.postValue(updatedList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun save(post: Post) {
        thread {
            try {
                val publishedMillis = if (post.id == 0L) {
                    //  новый пост: текущее время
                    System.currentTimeMillis()
                } else {
                    //  редактирование: сохраняем исходный published, если он известен
                    publishedById[post.id] ?: System.currentTimeMillis()
                }

                val apiPost = PostApiModel(
                    id = if (post.id == 0L) 0L else post.id, // ✅ новый: 0
                    author = post.author,
                    content = post.content,
                    published = publishedMillis,
                    likedByMe = post.likedByMe,
                    likes = post.likes,
                    shares = post.shares,
                    views = post.views,
                    video = post.video
                )

                val json = gson.toJson(apiPost)
                val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url("$BASE_URL/api/posts")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw RuntimeException("Error: ${response.code}")

                    val responseBody = response.body?.string() ?: throw RuntimeException("Empty body")
                    val savedApi = gson.fromJson(responseBody, PostApiModel::class.java)

                    // обновляем published map по ответу сервера
                    publishedById[savedApi.id] = savedApi.published

                    val savedUi = savedApi.toUi()
                    val current = _data.value.orEmpty()
                    val updated = if (current.any { it.id == savedUi.id }) {
                        current.map { if (it.id == savedUi.id) savedUi else it }
                    } else {
                        listOf(savedUi) + current
                    }

                    _data.postValue(updated)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun removeById(id: Long) {
        publishedById.remove(id)
        _data.value = _data.value.orEmpty().filter { it.id != id }
    }

    override fun share(id: Long) {
        _data.value = _data.value.orEmpty().map { p ->
            if (p.id == id) p.copy(shares = p.shares + 1) else p
        }
    }

    companion object {
        private const val BASE_URL = "http://127.0.0.1:9999"
        private val JSON = "application/json".toMediaType()
    }
}
