package applicationId.ru.netology.nmedia.dto

import retrofit2.Response
import retrofit2.http.*

interface PostsService {

    @GET("api/posts")
    suspend fun getAll(): Response<List<PostApiModel>>

    @POST("api/posts")
    suspend fun create(@Body post: PostApiModel): Response<PostApiModel>

    @PUT("api/posts/{id}")
    suspend fun update(@Path("id") id: Long, @Body post: PostApiModel): Response<PostApiModel>

    @GET("api/posts/latest")
    suspend fun getNewer(@Query("lastId") lastId: Long): Response<List<PostApiModel>>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<PostApiModel>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<PostApiModel>

    @DELETE("api/posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}