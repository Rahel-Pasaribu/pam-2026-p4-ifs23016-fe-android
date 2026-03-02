package org.delcom.pam_p4_ifs23016.network.books.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseBooks(
    val books: List<ResponseBookData>
)

@Serializable
data class ResponseBook(
    val book: ResponseBookData  // ✅ "book" huruf kecil, sesuai backend
)

@Serializable
data class ResponseBookAdd(
    val bookId: String
)

@Serializable
data class ResponseBookData(
    val id: String,
    val title: String,        // ✅ bukan "nama"
    val description: String,  // ✅ bukan "deskripsi"
    val genre: String,
    val mainCharacter: String, // ✅ bukan "karakterUtama"
    val author: String,        // ✅ bukan "penulis"
    val coverPath: String,     // ✅ tambahkan ini untuk gambar
    val createdAt: String,
    val updatedAt: String
)
