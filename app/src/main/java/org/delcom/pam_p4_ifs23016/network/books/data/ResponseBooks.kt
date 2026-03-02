package org.delcom.pam_p4_ifs23016.network.books.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseBooks(
    val books: List<ResponseBookData>
)

@Serializable
data class ResponseBook (
    val Book: ResponseBookData
)

@Serializable
data class ResponseBookAdd (
    val bookId: String
)

@Serializable
data class ResponseBookData(
    val id: String,
    val nama: String,
    val deskripsi: String,
    val genre: String,
    val karakterUtama: String,
    val penulis: String,
    val createdAt: String,
    val updatedAt: String
)