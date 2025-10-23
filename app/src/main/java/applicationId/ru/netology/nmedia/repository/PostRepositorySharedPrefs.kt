package applicationId.ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicationId.ru.netology.nmedia.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class PostRepositorySharedPrefs(context: Context) : PostRepository {
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            sync()
            _data.value = value // Обновляем LiveData при изменении posts
        }
    private var nextId = 1L
    private val _data = MutableLiveData(posts)

    init {
        prefs.getString(KEY_POSTS, null)?.let { json ->
            val loadedPosts = gson.fromJson<List<Post>>(json, type)
            posts = loadedPosts
            nextId = loadedPosts.maxOfOrNull { it.id }?.inc() ?: 1L

        }
    }

    private fun sync() {
        prefs.edit {
            putString(KEY_POSTS, gson.toJson(posts))
        }
    }

    override val data: LiveData<List<Post>>
        get() = _data

    override fun like(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(
                    likedByMe = !post.likedByMe,
                    likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
                )
            } else {
                post
            }
        }
    }

    override fun share(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(shares = post.shares + 1)
            } else {
                post
            }
        }
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
    }

    override fun save(post: Post) {
        posts = if (posts.any { it.id == post.id }) {
            posts.map { if (it.id == post.id) post else it }
        } else {
            listOf(post.copy(id = nextId++)) + posts
        }
    }

    companion object {
        private const val KEY_POSTS = "posts"
        private val gson = Gson()
        private val type = object : TypeToken<List<Post>>() {}.type
    }
}