package applicationId.ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import applicationId.ru.netology.nmedia.dto.Point

@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val lat: Double,
    val lon: Double,
) {
    fun toDto(): Point = Point(
        id = id,
        title = title,
        description = description,
        lat = lat,
        lon = lon,
    )

    companion object {
        fun fromDto(dto: Point): PointEntity = PointEntity(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            lat = dto.lat,
            lon = dto.lon,
        )
    }
}