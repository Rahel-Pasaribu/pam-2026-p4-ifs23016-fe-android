package org.delcom.pam_p4_ifs23016.network.books.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBook
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookAdd
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBooks

class BookRepository(private val bookApiService: BookApiService) : IBookRepository {


    override suspend fun getAllBooks(search: String?): ResponseMessage<ResponseBooks?> {
        return SuspendHelper.safeApiCall {
            bookApiService.getAllBooks(search)
        }
    }

    override suspend fun postBook(
        title: RequestBody,
        description: RequestBody,
        genre: RequestBody,
        mainCharacter: RequestBody,
        author: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponseBookAdd?> {
        return SuspendHelper.safeApiCall {
            bookApiService.postBook(
                title = title,
                description = description,
                genre = genre,
                mainCharacter = mainCharacter,
                author = author,
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
        title: RequestBody,
        description: RequestBody,
        genre: RequestBody,
        mainCharacter: RequestBody,
        author: RequestBody,
        file: MultipartBody.Part?
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            bookApiService.putBook(
                bookId = bookId,
                title = title,
                description = description,
                genre = genre,
                mainCharacter = mainCharacter,
                author = author,
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
