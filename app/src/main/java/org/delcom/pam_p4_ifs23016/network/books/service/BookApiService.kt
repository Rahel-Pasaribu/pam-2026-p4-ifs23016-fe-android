package org.delcom.pam_p4_ifs23016.network.books.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBook
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookAdd
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBooks
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {



    @GET("books")
    suspend fun getAllBooks(
        @Query("search") search: String? = null
    ): ResponseMessage<ResponseBooks?>

    @Multipart
    @POST("books") // ✅ tanpa leading slash "/"
    suspend fun postBook(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("mainCharacter") mainCharacter: RequestBody,
        @Part("author") author: RequestBody,
        @Part file: MultipartBody.Part
    ): ResponseMessage<ResponseBookAdd?>

    @GET("books/{bookId}")
    suspend fun getBookById(
        @Path("bookId") bookId: String
    ): ResponseMessage<ResponseBook?>

    @Multipart
    @PUT("books/{bookId}") // ✅ tanpa leading slash "/"
    suspend fun putBook(
        @Path("bookId") bookId: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("mainCharacter") mainCharacter: RequestBody,
        @Part("author") author: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): ResponseMessage<String?>

    @DELETE("books/{bookId}") // ✅ konsisten {bookId}
    suspend fun deleteBook(
        @Path("bookId") bookId: String // ✅ nama path cocok
    ): ResponseMessage<String?>
}
