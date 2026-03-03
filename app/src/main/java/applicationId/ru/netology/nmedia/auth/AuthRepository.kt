package applicationId.ru.netology.nmedia.auth

import kotlinx.coroutines.flow.Flow

data class AuthState(
    val userId: Long? = null,
    val isAuthenticated: Boolean = false
)

interface AuthRepository {
    val authState: Flow<AuthState>
    suspend fun login(userId: Long)
    suspend fun logout()
}