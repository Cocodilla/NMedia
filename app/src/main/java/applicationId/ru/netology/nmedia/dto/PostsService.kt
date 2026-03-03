package applicationId.ru.netology.nmedia.dto

import retrofit2.Response
import retrofit2.http.*

interface PostsService {

    @GET("api/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<PostApiModel>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostApiModel>>

    @POST("api/posts")
    suspend fun save(@Body post: PostApiModel): Response<PostApiModel>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<PostApiModel>

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<PostApiModel>

    @DELETE("api/posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}