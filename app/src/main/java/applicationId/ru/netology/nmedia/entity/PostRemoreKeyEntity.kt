package applicationId.ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * id у последнего элемента"
 */
@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey val type: String,
    val lastId: Long
)