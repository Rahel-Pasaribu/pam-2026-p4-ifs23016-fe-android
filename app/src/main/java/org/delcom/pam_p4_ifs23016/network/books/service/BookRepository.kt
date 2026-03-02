package org.delcom.pam_p4_ifs23016.network.books.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBook
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookAdd
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBooks
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseProfile

class BookRepository (private val bookApiService: BookApiService): IBookRepository {
    override suspend fun getProfile(): ResponseMessage<ResponseProfile?> {
        return SuspendHelper.safeApiCall {
            bookApiService.getProfile()
        }
    }

    override suspend fun getAllBooks(search: String?): ResponseMessage<ResponseBooks?> {
        return SuspendHelper.safeApiCall {
            bookApiService.getAllBooks(search)
        }
    }

    override suspend fun postBook(
        nama: RequestBody,
        deskripsi: RequestBody,
        genre: RequestBody,
        karakterUtama: RequestBody,
        penulis: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponseBookAdd?> {
        return SuspendHelper.safeApiCall {
            bookApiService.postBook(
                nama = nama,
                deskripsi = deskripsi,
                genre = genre,
                karakterUtama = karakterUtama,
                penulis = penulis,
                file = file
            )
        }
    }

    override suspend fun getBookById(bookId: String): ResponseMessage<ResponseBook?> {
        return SuspendHelper.safeApiCall {
            bookApiService.getBookById(bookId)
        }
    }

    override suspend fun putBook(
        bookId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        genre: RequestBody,
        karakterUtama: RequestBody,
        penulis: RequestBody,
        file: MultipartBody.Part?
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            bookApiService.putBook(
                bookId = bookId,
                nama = nama,
                deskripsi = deskripsi,
                genre = genre,
                karakterUtama = karakterUtama,
                penulis = penulis,
                file = file
            )
        }
    }

    override suspend fun deleteBook(bookId: String): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            bookApiService.deleteBook(bookId)
        }
    }
}