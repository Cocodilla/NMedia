package applicationId.ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import applicationId.ru.netology.nmedia.dto.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class PostRepositoryFilesImpl(private val context: Context) : PostRepository {
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            sync()
            _data.value = value
        }
    private var nextId = 1L
    private val _data = MutableLiveData(posts)

    init {
        loadFromFile()
    }

    private fun loadFromFile() {
        val file = context.filesDir.resolve(FILENAME)
        if (file.exists()) {
            try {
                context.openFileInput(FILENAME).bufferedReader().use { reader ->
                    val json = reader.readText()
                    if (json.isNotBlank()) {
                        val loadedPosts = Gson().fromJson<List<Post>>(json, type)
                        posts = loadedPosts
                        nextId = (loadedPosts.maxOfOrNull { it.id } ?: 0L) + 1L
                    } else {
                        // Файл существует, но пустой
                        posts = emptyList()
                        nextId = 1L
                    }
                }
            } catch (e: IOException) {
                // Если ошибка чтения, инициализируем пустым списком
                posts = emptyList()
                nextId = 1L
            }
        } else {
            // Файл не существует - первый запуск
            posts = emptyList()
            nextId = 1L
        }
    }

    private fun sync() {
        try {
            context.openFileOutput(FILENAME, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
                writer.write(Gson().toJson(posts))
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
            // Редактирование существующего поста
            posts.map { if (it.id == post.id) post else it }
        } else {
            // Создание нового поста
            listOf(post.copy(id = nextId++)) + posts
        }
    }

    companion object {
        private const val FILENAME = "posts.json"
        private val type = object : TypeToken<List<Post>>() {}.type
    }
}