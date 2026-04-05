package applicationId.ru.netology.nmedia.dto

data class Point(
    val id: Long = 0,
    val title: String,
    val description: String,
    val lat: Double,
    val lon: Double,
)
