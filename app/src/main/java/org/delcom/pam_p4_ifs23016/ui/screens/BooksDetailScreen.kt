package org.delcom.pam_p4_ifs23016.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23016.R
import org.delcom.pam_p4_ifs23016.helper.ConstHelper
import org.delcom.pam_p4_ifs23016.helper.RouteHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookData
import org.delcom.pam_p4_ifs23016.ui.components.BottomDialog
import org.delcom.pam_p4_ifs23016.ui.components.BottomDialogType
import org.delcom.pam_p4_ifs23016.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23016.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23016.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23016.ui.components.TopAppBarMenuItem
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookActionUIState
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookUIState
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookViewModel

@Composable
fun booksDetailScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    BookViewModel: BookViewModel,
    bookId: String
) {
    val uiStateBook by BookViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var isConfirmDelete by remember { mutableStateOf(false) }
    var Book by remember { mutableStateOf<ResponseBookData?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        uiStateBook.BookAction = BookActionUIState.Loading
        uiStateBook.Book = BookUIState.Loading
        BookViewModel.getBookById(bookId)
    }

    LaunchedEffect(uiStateBook.Book) {
        if (uiStateBook.Book !is BookUIState.Loading) {
            if (uiStateBook.Book is BookUIState.Success) {
                Book = (uiStateBook.Book as BookUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
            }
        }
    }

    fun onDelete() {
        isLoading = true
        BookViewModel.deleteBook(bookId)
    }

    LaunchedEffect(uiStateBook.BookAction) {
        when (val state = uiStateBook.BookAction) {
            is BookActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost = snackbarHost, type = SnackBarType.SUCCESS, message = state.message)
                RouteHelper.to(navController, ConstHelper.RouteNames.books.path, true)
                uiStateBook.Book = BookUIState.Loading
                isLoading = false
            }
            is BookActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost = snackbarHost, type = SnackBarType.ERROR, message = state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading || Book == null) {
        LoadingUI()
        return
    }

    val detailMenuItems = listOf(
        TopAppBarMenuItem(
            text = "Ubah Data", icon = Icons.Filled.Edit, route = null,
            onClick = { RouteHelper.to(navController, ConstHelper.RouteNames.booksEdit.path.replace("{bookId}", Book!!.id)) }
        ),
        TopAppBarMenuItem(
            text = "Hapus Data", icon = Icons.Filled.Delete, route = null,
            onClick = { isConfirmDelete = true }
        ),
    )

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(
            navController = navController,
            title = Book!!.title, // ✅ bukan Book!!.nama
            showBackButton = true,
            customMenuItems = detailMenuItems
        )
        Box(modifier = Modifier.weight(1f)) {
            booksDetailUI(Book = Book!!)
            BottomDialog(
                type = BottomDialogType.ERROR,
                show = isConfirmDelete,
                onDismiss = { isConfirmDelete = false },
                title = "Konfirmasi Hapus Data",
                message = "Apakah Anda yakin ingin menghapus data ini?",
                confirmText = "Ya, Hapus",
                onConfirm = { onDelete() },
                cancelText = "Batal",
                destructiveAction = true
            )
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun booksDetailUI(Book: ResponseBookData) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        // Gambar & Judul
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 16.dp)) {
            AsyncImage(
                model = ToolsHelper.getBookImageUrl(Book.id),
                contentDescription = Book.title, // ✅
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                modifier = Modifier.fillMaxWidth().height(250.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = Book.title, // ✅ bukan Book.nama
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Deskripsi
        BookDetailCard(label = "Deskripsi", value = Book.description) // ✅

        // Genre
        BookDetailCard(label = "Genre", value = Book.genre)

        // Karakter Utama
        BookDetailCard(label = "Karakter Utama", value = Book.mainCharacter) // ✅

        // Penulis
        BookDetailCard(label = "Penulis", value = Book.author) // ✅
    }
}

@Composable
fun BookDetailCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
