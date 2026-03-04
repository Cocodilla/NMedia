package applicationId.ru.netology.nmedia.auth

import applicationId.ru.netology.nmedia.di.ApplicationScope
import applicationId.ru.netology.nmedia.dto.AuthRequest
import applicationId.ru.netology.nmedia.dto.AuthService
import applicationId.ru.netology.nmedia.error.ApiError
import applicationId.ru.netology.nmedia.error.NetworkError
import applicationId.ru.netology.nmedia.error.UnknownError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService,
    private val local: AuthLocalDataSource,
    @ApplicationScope private val appScope: CoroutineScope
) : AuthRepository {

    private val _authState = MutableStateFlow(AuthState())
    override val authState: StateFlow<AuthState> = _authState

    init {
        // Поддерживаем StateFlow актуальным из DataStore
        appScope.launch {
            local.authState.collectLatest { state ->
                _authState.value = state
            }
        }
    }

    override suspend fun login(login: String, pass: String) {
        try {
            val response = service.authenticate(login, pass)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            local.setAuth(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: ApiError) {
            throw e
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun logout() {
        local.clear()

    }
}