package applicationId.ru.netology.nmedia.dto

data class AuthRequest(
    val login: String,
    val pass: String
)

data class AuthResponse(
    val id: Long,
    val token: String
)