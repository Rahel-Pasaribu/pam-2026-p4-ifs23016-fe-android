package org.delcom.pam_p4_ifs23016.network.books.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBook
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookAdd
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBooks

interface IBookRepository {



    suspend fun getAllBooks(
        search: String? = null
    ): ResponseMessage<ResponseBooks?>

    suspend fun postBook(
        title: RequestBody,
        description: RequestBody,
        genre: RequestBody,
        mainCharacter: RequestBody,
        author: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponseBookAdd?>

    suspend fun getBookById(
        bookId: String
    ): ResponseMessage<ResponseBook?>

    suspend fun putBook(
        bookId: String,
        title: RequestBody,
        description: RequestBody,
        genre: RequestBody,
        mainCharacter: RequestBody,
        author: RequestBody,
        file: MultipartBody.Part?
    ): ResponseMessage<String?>

    suspend fun deleteBook(
        bookId: String
    ): ResponseMessage<String?>
}
