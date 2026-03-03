package applicationId.ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import applicationId.ru.netology.nmedia.auth.AuthRepository
import applicationId.ru.netology.nmedia.error.AppError
import applicationId.ru.netology.nmedia.error.UnknownError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val loading: Boolean = false,
    val error: AppError? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState = authRepository.authState

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(login: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(loading = true)
            try {
                authRepository.login(login, pass)
                _uiState.value = AuthUiState()
            } catch (e: AppError) {
                _uiState.value = AuthUiState(error = e)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = UnknownError)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}