package applicationId.ru.netology.nmedia.dto

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PostApiModel(
    val id: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Long, // seconds (unix time)
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null,
    val attachment: AttachmentApiModel? = null
)

data class AttachmentApiModel(
    val url: String,
    val description: String,
    val type: String
)

/* ---------- formatting ---------- */

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

/* ---------- API -> UI ---------- */

fun PostApiModel.toUi(): Post = Post(
    id = id,
    author = author,
    authorAvatar = authorAvatar,
    content = content,
    published = humanDate(published),
    likedByMe = likedByMe,
    likes = likes,
    shares = shares,
    views = views,
    video = video,
    attachment = attachment?.let {
        Post.Attachment(
            url = it.url,
            description = it.description,
            type = it.type
        )
    }
)

/* ---------- UI -> API (save/edit) ---------- */
fun Post.toApiForSave(): PostApiModel = PostApiModel(
    id = if (id == 0L) 0L else id,
    author = author.ifBlank { "Me" },
    authorAvatar = authorAvatar,
    content = content,
    published = System.currentTimeMillis() / 1000, // server seconds
    likedByMe = likedByMe,
    likes = likes,
    shares = shares,
    views = views,
    video = video,
    attachment = attachment?.let { AttachmentApiModel(it.url, it.description, it.type) }
)
