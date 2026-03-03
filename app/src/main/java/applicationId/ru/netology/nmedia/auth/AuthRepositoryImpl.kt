package applicationId.ru.netology.nmedia.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    private val _authState = MutableStateFlow(AuthState())
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    override suspend fun login(userId: Long) {
        _authState.value = AuthState(userId = userId, isAuthenticated = true)
    }

    override suspend fun logout() {
        _authState.value = AuthState()
    }
}