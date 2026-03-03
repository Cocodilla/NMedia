package applicationId.ru.netology.nmedia.dto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/users/authentication")
    suspend fun authenticate(@Body request: AuthRequest): Response<AuthResponse>
}