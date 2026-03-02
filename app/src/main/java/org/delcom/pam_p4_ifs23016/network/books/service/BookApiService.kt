package org.delcom.pam_p4_ifs23016.network.books.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBook
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookAdd
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBooks
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseProfile
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {
    // Ambil profile developer
    @GET("profile")
    suspend fun getProfile(): ResponseMessage<ResponseProfile?>

    // Ambil semua data tumbuhan
    @GET("books")
    suspend fun getAllBooks(
        @Query("search") search: String? = null
    ): ResponseMessage<ResponseBooks?>

    // Tambah data tumbuhan
    @Multipart
    @POST("/books")
    suspend fun postBook(
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("karakterUtama") karakterUtama: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part file: MultipartBody.Part
    ): ResponseMessage<ResponseBookAdd?>

    // Ambil data tumbuhan berdasarkan ID
    @GET("books/{bookId}")
    suspend fun getBookById(
        @Path("bookId") bookId: String
    ): ResponseMessage<ResponseBook?>


    // Ubah data tumbuhan
    @Multipart
    @PUT("/books/{bookId}")
    suspend fun putBook(
        @Path("bookId") bookId: String,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("karakterUtama") karakterUtama: RequestBody,
        @Part("penulis") penulis: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): ResponseMessage<String?>

    // Hapus data tumbuhan
    @DELETE("books/{booksId}")
    suspend fun deleteBook(
        @Path("bookId") bookId: String
    ): ResponseMessage<String?>
}