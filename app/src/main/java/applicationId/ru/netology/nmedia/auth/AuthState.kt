package applicationId.ru.netology.nmedia.auth
/**
 * Текущее состояние авторизации.
 * id = 0 и token = null -> не авторизован.
 */
data class AuthState(
    val id: Long = 0L,
    val token: String? = null
) {
    val isAuthorized: Boolean get() = !token.isNullOrBlank()
}