package applicationId.ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Attachment(
    val url: String,
    val description: String? = null,
    val type: AttachmentType
) : Parcelable
    enum class AttachmentType {
        IMAGE
    }

