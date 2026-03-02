package org.delcom.pam_p4_ifs23016.ui.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookData
import org.delcom.pam_p4_ifs23016.network.books.service.IBookRepository
import javax.inject.Inject


sealed interface booksUIState {
    data class Success(val data: List<ResponseBookData>) : booksUIState
    data class Error(val message: String) : booksUIState
    object Loading : booksUIState
}

sealed interface BookUIState {
    data class Success(val data: ResponseBookData) : BookUIState
    data class Error(val message: String) : BookUIState
    object Loading : BookUIState
}

sealed interface BookActionUIState {
    data class Success(val message: String) : BookActionUIState
    data class Error(val message: String) : BookActionUIState
    object Loading : BookActionUIState
}

data class UIStateBook(
    val profile: ProfileUIState = ProfileUIState.Loading,
    val books: booksUIState = booksUIState.Loading,
    var Book: BookUIState = BookUIState.Loading,
    var BookAction: BookActionUIState = BookActionUIState.Loading
)

@HiltViewModel
@Keep
class BookViewModel @Inject constructor(
    private val repository: IBookRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UIStateBook())
    val uiState = _uiState.asStateFlow()


    fun getAllBooks(search: String? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    books = booksUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.getAllBooks(search)
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            booksUIState.Success(it.data!!.books)
                        } else {
                            booksUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        booksUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    books = tmpState
                )
            }
        }
    }

    fun postBook(
        nama: RequestBody,
        deskripsi: RequestBody,
        genre: RequestBody,
        karakterUtama: RequestBody,
        penulis: RequestBody,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    BookAction = BookActionUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.postBook(
                        nama = nama,
                        deskripsi = deskripsi,
                        genre = genre,
                        karakterUtama = karakterUtama,
                        penulis = penulis,
                        file = file
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            BookActionUIState.Success(it.data!!.bookId)
                        } else {
                            BookActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        BookActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    BookAction = tmpState
                )
            }
        }
    }

    fun getBookById(bookId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    Book = BookUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.getBookById(bookId)
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            BookUIState.Success(it.data!!.Book)
                        } else {
                            BookUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        BookUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    Book = tmpState
                )
            }
        }
    }

    fun putBook(
        bookId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        genre: RequestBody,
        karakterUtama: RequestBody,
        penulis: RequestBody,
        file: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    BookAction = BookActionUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.putBook(
                        bookId = bookId,
                        nama = nama,
                        deskripsi = deskripsi,
                        genre = genre,
                        karakterUtama = karakterUtama,
                        penulis = penulis,
                        file = file
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            BookActionUIState.Success(it.message)
                        } else {
                            BookActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        BookActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    BookAction = tmpState
                )
            }
        }
    }

    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    BookAction = BookActionUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.deleteBook(
                        bookId = bookId
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            BookActionUIState.Success(it.message)
                        } else {
                            BookActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        BookActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    BookAction = tmpState
                )
            }
        }
    }
}