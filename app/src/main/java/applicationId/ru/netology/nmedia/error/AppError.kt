package applicationId.ru.netology.nmedia.error

sealed class AppError(message: String? = null) : RuntimeException(message)

class ApiError(val code: Int, message: String) : AppError("API $code: $message")
object NetworkError : AppError("Network error")
object UnknownError : AppError("Unknown error")

