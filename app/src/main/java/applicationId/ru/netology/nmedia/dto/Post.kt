package applicationId.ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Parcelize
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val publishedTimestamp: Long, // unix seconds
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null,
    val authorAvatar: String? = null,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false
) : Parcelable {
    val published: String
        get() = humanDate(publishedTimestamp)

    companion object {
        private val localeRu = Locale("ru")
        private val timeFmt = DateTimeFormatter.ofPattern("HH:mm", localeRu)
        private val dayMonthFmt = DateTimeFormatter.ofPattern("dd MMM", localeRu)
        private val fullFmt = DateTimeFormatter.ofPattern("dd MMM yyyy", localeRu)

        private fun humanDate(seconds: Long): String {
            val zone = ZoneId.systemDefault()
            val dt = Instant.ofEpochSecond(seconds).atZone(zone)
            val date = dt.toLocalDate()
            val today = LocalDate.now(zone)
            val yesterday = today.minusDays(1)
            return when {
                date == today -> "сегодня в ${dt.format(timeFmt)}"
                date == yesterday -> "вчера в ${dt.format(timeFmt)}"
                date.year == today.year -> "${dt.format(dayMonthFmt)} в ${dt.format(timeFmt)}"
                else -> "${dt.format(fullFmt)} в ${dt.format(timeFmt)}"
            }
        }
    }
}