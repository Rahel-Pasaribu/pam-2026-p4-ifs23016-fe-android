package org.delcom.pam_p4_ifs23016.network.books.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBook
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookAdd
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBooks
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseProfile

interface IBookRepository {
    // Ambil profile developer
    suspend fun getProfile(): ResponseMessage<ResponseProfile?>

    // Ambil semua data tumbuhan
    suspend fun getAllBooks(
        search: String? = null
    ): ResponseMessage<ResponseBooks?>

    // Tambah data tumbuhan
    suspend fun postBook(
        nama: RequestBody,
        deskripsi: RequestBody,
        genre: RequestBody,
        karakterUtama: RequestBody,
        penulis: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponseBookAdd?>

    // Ambil data tumbuhan berdasarkan ID
    suspend fun getBookById(
        bookId: String
    ): ResponseMessage<ResponseBook?>


    // Ubah data tumbuhan
    suspend fun putBook(
        bookId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        genre: RequestBody,
        karakterUtama: RequestBody,
        penulis: RequestBody,
        file: MultipartBody.Part? = null
    ): ResponseMessage<String?>

    // Hapus data tumbuhan
    suspend fun deleteBook(
        bookId: String
    ): ResponseMessage<String?>
}