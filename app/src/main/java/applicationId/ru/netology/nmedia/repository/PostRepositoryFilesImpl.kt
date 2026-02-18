package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dao.PostDao
import applicationId.ru.netology.nmedia.dto.Post
import applicationId.ru.netology.nmedia.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {

    override val data: Flow<List<Post>> =
        dao.getVisible().map { list -> list.map { it.toDto() } }

    override val newerCount: Flow<Int> = dao.countHidden()

    override suspend fun refresh() {
        // пока что просто заглушка: если у тебя нет API — не падаем
        // позже сюда подключим Retrofit и будем insert(...)
    }

    override suspend fun getNewer() {
        // заглушка под дальнейший Retrofit:
        // должны прийти новые посты и сохраниться в БД с visible = false
        // dao.insert(list.map { PostEntity.fromDto(it, visible = false) })
    }

    override suspend fun showNewer() {
        dao.showAllHidden()
    }

    override suspend fun save(content: String) {
        // заглушка: без API мы не можем получить id от сервера
        // временно не реализуем, чтобы проект собирался
        // (если нужно — сделаю локальное сохранение)
    }

    override suspend fun editById(id: Long, content: String) {
        // заглушка
    }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
        // позже: вызов API, retry и т.д.
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        // позже: вызов API, retry и т.д.
    }
}
