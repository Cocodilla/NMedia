package applicationId.ru.netology.nmedia.repository

import applicationId.ru.netology.nmedia.dto.Point
import kotlinx.coroutines.flow.Flow

interface PointRepository {
    fun data(): Flow<List<Point>>
    suspend fun save(point: Point)
    suspend fun removeById(id: Long)
}