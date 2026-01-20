package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dto.Post

interface PostRepository {

    interface Callback<T> {
        fun onSuccess(value: T)
        fun onError(e: Exception)
    }

    fun getAll(callback: Callback<List<Post>>)

    fun likeById(id: Long, callback: Callback<Post>)
    fun unlikeById(id: Long, callback: Callback<Post>)

    fun save(content: String, callback: Callback<Post>)

    fun editById(id: Long, content: String, callback: Callback<Post>)

    fun removeById(id: Long, callback: Callback<Unit>)
}
