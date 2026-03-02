package org.delcom.pam_p4_ifs23016.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import okhttp3.MultipartBody
import org.delcom.pam_p4_ifs23016.R
import org.delcom.pam_p4_ifs23016.helper.AlertHelper
import org.delcom.pam_p4_ifs23016.helper.AlertState
import org.delcom.pam_p4_ifs23016.helper.AlertType
import org.delcom.pam_p4_ifs23016.helper.ConstHelper
import org.delcom.pam_p4_ifs23016.helper.RouteHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper.toRequestBodyText
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper.uriToMultipart
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookData
import org.delcom.pam_p4_ifs23016.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23016.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23016.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookActionUIState
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookUIState
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookViewModel

@Composable
fun booksEditScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    BookViewModel: BookViewModel,
    bookId: String
) {
    val uiStateBook by BookViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
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
                isLoading = false
            }
        }
    }

    // ✅ Semua parameter sesuai nama field backend
    fun onSave(
        context: Context,
        title: String,
        description: String,
        genre: String,
        mainCharacter: String,
        author: String,
        file: Uri? = null
    ) {
        isLoading = true

        val titleBody = title.toRequestBodyText()
        val descriptionBody = description.toRequestBodyText()
        val genreBody = genre.toRequestBodyText()
        val mainCharacterBody = mainCharacter.toRequestBodyText()
        val authorBody = author.toRequestBodyText()

        var filePart: MultipartBody.Part? = null
        if (file != null) {
            filePart = uriToMultipart(context, file, "file")
        }

        BookViewModel.putBook(
            bookId = bookId,
            title = titleBody,
            description = descriptionBody,
            genre = genreBody,
            mainCharacter = mainCharacterBody,
            author = authorBody,
            file = filePart,
        )
    }

    LaunchedEffect(uiStateBook.BookAction) {
        when (val state = uiStateBook.BookAction) {
            is BookActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost = snackbarHost, type = SnackBarType.SUCCESS, message = state.message)
                RouteHelper.to(
                    navController = navController,
                    destination = ConstHelper.RouteNames.booksDetail.path.replace("{bookId}", bookId),
                    popUpTo = ConstHelper.RouteNames.booksDetail.path.replace("{bookId}", bookId),
                    removeBackStack = true
                )
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

    Column(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBarComponent(navController = navController, title = "Ubah Data", showBackButton = true)
        Box(modifier = Modifier.weight(1f)) {
            booksEditUI(Book = Book!!, onSave = ::onSave)
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun booksEditUI(
    Book: ResponseBookData,
    onSave: (Context, String, String, String, String, String, Uri?) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }

    var dataFile by remember { mutableStateOf<Uri?>(null) }
    // ✅ Gunakan field yang sesuai backend
    var dataTitle by remember { mutableStateOf(Book.title) }
    var dataDescription by remember { mutableStateOf(Book.description) }
    var dataGenre by remember { mutableStateOf(Book.genre) }
    var dataMainCharacter by remember { mutableStateOf(Book.mainCharacter) }
    var dataAuthor by remember { mutableStateOf(Book.author) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> dataFile = uri }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Gambar
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = if (dataFile != null) dataFile else ToolsHelper.getBookImageUrl(Book.id),
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder),
                    contentDescription = "Pratinjau Gambar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tap untuk mengganti gambar", style = MaterialTheme.typography.bodySmall)
        }

        // Title
        OutlinedTextField(
            value = dataTitle, onValueChange = { dataTitle = it },
            label = { Text("Judul Buku", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        // Description
        OutlinedTextField(
            value = dataDescription, onValueChange = { dataDescription = it },
            label = { Text("Deskripsi", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            maxLines = 5, minLines = 3
        )

        // Genre
        OutlinedTextField(
            value = dataGenre, onValueChange = { dataGenre = it },
            label = { Text("Genre", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        // Main Character
        OutlinedTextField(
            value = dataMainCharacter, onValueChange = { dataMainCharacter = it },
            label = { Text("Karakter Utama", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        // Author
        OutlinedTextField(
            value = dataAuthor, onValueChange = { dataAuthor = it },
            label = { Text("Penulis", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(64.dp))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                if (dataTitle.isEmpty()) { AlertHelper.show(alertState, AlertType.ERROR, "Judul tidak boleh kosong!"); return@FloatingActionButton }
                if (dataDescription.isEmpty()) { AlertHelper.show(alertState, AlertType.ERROR, "Deskripsi tidak boleh kosong!"); return@FloatingActionButton }
                if (dataGenre.isEmpty()) { AlertHelper.show(alertState, AlertType.ERROR, "Genre tidak boleh kosong!"); return@FloatingActionButton }
                if (dataMainCharacter.isEmpty()) { AlertHelper.show(alertState, AlertType.ERROR, "Karakter utama tidak boleh kosong!"); return@FloatingActionButton }
                if (dataAuthor.isEmpty()) { AlertHelper.show(alertState, AlertType.ERROR, "Penulis tidak boleh kosong!"); return@FloatingActionButton }
                onSave(context, dataTitle, dataDescription, dataGenre, dataMainCharacter, dataAuthor, dataFile)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Simpan Data")
        }
    }

    if (alertState.value.isVisible) {
        AlertDialog(
            onDismissRequest = { AlertHelper.dismiss(alertState) },
            title = { Text(alertState.value.type.title) },
            text = { Text(alertState.value.message) },
            confirmButton = { TextButton(onClick = { AlertHelper.dismiss(alertState) }) { Text("OK") } }
        )
    }
}
