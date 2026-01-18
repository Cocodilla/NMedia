package applicationId.ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicationId.ru.netology.nmedia.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient()
    private val gson = Gson()

    private var posts: List<Post> = emptyList()
        set(value) {
            field = value
            _data.postValue(value)
        }

    private val _data = MutableLiveData<List<Post>>(emptyList())
    override val data: LiveData<List<Post>>
        get() = _data

    init {
        refresh() // сразу грузим с сервера
    }
     // Загрузка всех постов с сервера.

    fun refresh() {
        thread {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL/api/posts")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw RuntimeException("Error: ${response.code}")
                    }

                    val body = response.body?.string()
                        ?: throw RuntimeException("Empty body")

                    val type = object : TypeToken<List<Post>>() {}.type
                    val loaded = gson.fromJson<List<Post>>(body, type)

                    posts = loaded
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


     // Сервер возвращает JSON обновлённого поста -> заменяем пост в списке.

    override fun like(id: Long) {
        val current = _data.value.orEmpty()
        val target = current.find { it.id == id } ?: return

        thread {
            try {
                val request = if (target.likedByMe) {
                    // снять лайк
                    Request.Builder()
                        .url("$BASE_URL/api/posts/$id/likes")
                        .delete()
                        .build()
                } else {
                    // поставить лайк
                    Request.Builder()
                        .url("$BASE_URL/api/posts/$id/likes")
                        .post(ByteArray(0).toRequestBody())
                        .build()
                }

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw RuntimeException("Error: ${response.code}")
                    }

                    val body = response.body?.string()
                        ?: throw RuntimeException("Empty body")

                    val updated = gson.fromJson(body, Post::class.java)

                    // обновляем список: заменяем один элемент (актуальные лайки/likedByMe)
                    posts = _data.value.orEmpty().map { p ->
                        if (p.id == updated.id) updated else p
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun share(id: Long) {
        posts = _data.value.orEmpty().map { post ->
            if (post.id == id) post.copy(shares = post.shares + 1) else post
        }
    }

    override fun removeById(id: Long) {
        posts = _data.value.orEmpty().filter { it.id != id }
    }

    override fun save(post: Post) {
        val current = _data.value.orEmpty()
        posts = if (current.any { it.id == post.id }) {
            current.map { if (it.id == post.id) post else it }
        } else {
            listOf(post) + current
        }
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
    }
}
