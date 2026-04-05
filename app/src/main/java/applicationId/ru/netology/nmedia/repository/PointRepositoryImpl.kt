package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dao.PointDao
import applicationId.ru.netology.nmedia.dto.Point
import applicationId.ru.netology.nmedia.entity.PointEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PointRepositoryImpl @Inject constructor(
    private val dao: PointDao,
) : PointRepository {

    override fun data(): Flow<List<Point>> =
        dao.getAll().map { list -> list.map { it.toDto() } }

    override suspend fun save(point: Point) {
        dao.upsert(PointEntity.fromDto(point))
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }
}