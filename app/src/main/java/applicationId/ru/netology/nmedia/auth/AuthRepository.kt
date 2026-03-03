package applicationId.ru.netology.nmedia.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun login(login: String, pass: String)
    suspend fun logout()
}