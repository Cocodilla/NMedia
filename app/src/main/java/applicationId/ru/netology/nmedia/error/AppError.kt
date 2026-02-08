package applicationId.ru.netology.nmedia.error

sealed class AppError(message: String) : RuntimeException(message) {
    class Network : AppError("Ошибка сети")
    class Api(val code: Int) : AppError("Ошибка сервера: $code")
    class Unknown : AppError("Неизвестная ошибка")
}
