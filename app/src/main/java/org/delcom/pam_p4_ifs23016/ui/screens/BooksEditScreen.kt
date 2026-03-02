package org.delcom.pam_p4_ifs23016.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
    // Ambil data dari viewmodel
    val uiStateBook by BookViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    // Muat data
    var Book by remember { mutableStateOf<ResponseBookData?>(null) }

    // Dapatkan tumbuhan berdasarkan ID
    LaunchedEffect(Unit) {
        isLoading = true
        // Reset status Book action
        uiStateBook.BookAction = BookActionUIState.Loading
        uiStateBook.Book = BookUIState.Loading
        BookViewModel.getBookById(bookId)
    }

    // Picu ulang ketika data tumbuhan berubah
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

    // Simpan perubahan data
    fun onSave(
        context: Context,
        nama: String,
        deskripsi: String,
        genre: String,
        karakterUtama: String,
        penulis: String,
        file: Uri? = null
    ) {
        isLoading = true

        val namaBody = nama.toRequestBodyText()
        val deskripsiBody = deskripsi.toRequestBodyText()
        val genreBody = genre.toRequestBodyText()
        val karakterBody = karakterUtama.toRequestBodyText()
        val penulisBody = penulis.toRequestBodyText()

        var filePart: MultipartBody.Part? = null
        if (file != null) {
            filePart = uriToMultipart(context, file, "file")
        }

        BookViewModel.putBook(
            bookId = bookId,
            nama = namaBody,
            deskripsi = deskripsiBody,
            genre = genreBody,
            karakterUtama = karakterBody,
            penulis = penulisBody,
            file = filePart,
        )
    }

    LaunchedEffect(uiStateBook.BookAction) {
        when (val state = uiStateBook.BookAction) {
            is BookActionUIState.Success -> {
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.SUCCESS,
                    message = state.message
                )
                RouteHelper.to(
                    navController = navController,
                    destination = ConstHelper.RouteNames.booksDetail.path
                        .replace("{bookId}", bookId),
                    popUpTo = ConstHelper.RouteNames.booksDetail.path
                        .replace("{bookId}", bookId),
                    removeBackStack = true
                )
                isLoading = false
            }

            is BookActionUIState.Error -> {
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.ERROR,
                    message = state.message
                )
                isLoading = false
            }

            else -> {}
        }
    }

    // Tampilkan halaman loading
    if (isLoading || Book == null) {
        LoadingUI()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBarComponent(
            navController = navController,
            title = "Ubah Data",
            showBackButton = true,
        )
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            booksEditUI(
                Book = Book!!,
                onSave = ::onSave
            )
        }
        // Bottom Nav
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun booksEditUI(
    Book: ResponseBookData,
    onSave: (
        Context,
        String,
        String,
        String,
        String,
        String,
        Uri?
    ) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }

    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataNama by remember { mutableStateOf(Book.nama) }
    var dataDeskripsi by remember { mutableStateOf(Book.deskripsi) }
    var dataGenre by remember { mutableStateOf(Book.genre) }
    var dataKarakterUtama by remember { mutableStateOf(Book.karakterUtama) }
    var dataPenulis by remember { mutableStateOf(Book.penulis) }
    val context = LocalContext.current

    // Focus manager
    val focusManager = LocalFocusManager.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        dataFile = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // File Gambar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable {
                        imagePicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (dataFile != null) {
                    AsyncImage(
                        model = dataFile,
                        placeholder = painterResource(R.drawable.img_placeholder),
                        error = painterResource(R.drawable.img_placeholder),
                        contentDescription = "Pratinjau Gambar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = ToolsHelper.getBookImageUrl(Book.id),
                        placeholder = painterResource(R.drawable.img_placeholder),
                        error = painterResource(R.drawable.img_placeholder),
                        contentDescription = "Pratinjau Gambar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap untuk mengganti gambar",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Nama
        OutlinedTextField(
            value = dataNama,
            onValueChange = { dataNama = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                cursorColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            label = {
                Text(
                    text = "Nama",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
        )

        // Deskripsi
        OutlinedTextField(
            value = dataDeskripsi,
            onValueChange = { dataDeskripsi = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                cursorColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            label = {
                Text(
                    text = "Deskripsi",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            maxLines = 5,
            minLines = 3
        )

        // genre
        OutlinedTextField(
            value = dataGenre,
            onValueChange = { dataGenre = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                cursorColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            label = {
                Text(
                    text = "Genre",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            maxLines = 5,
            minLines = 3
        )

        // Karakter utama
        OutlinedTextField(
            value = dataKarakterUtama,
            onValueChange = { dataKarakterUtama = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                cursorColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            label = {
                Text(
                    text = "Karakter utama",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            maxLines = 5,
            minLines = 3
        )

        // Penulis
        OutlinedTextField(
            value = dataPenulis,
            onValueChange = { dataPenulis = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                cursorColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            label = {
                Text(
                    text = "Penulis",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            maxLines = 5,
            minLines = 3
        )

        Spacer(modifier = Modifier.height(64.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Floating Action Button
        FloatingActionButton(
            onClick = {
                if (dataNama.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Nama tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                if (dataDeskripsi.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Deskripsi tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                if (dataGenre.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Informasi genre tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                if (dataKarakterUtama.isEmpty()) {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Informasi Karakter utama tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

                onSave(
                    context,
                    dataNama,
                    dataDeskripsi,
                    dataGenre,
                    dataKarakterUtama,
                    dataPenulis,
                    dataFile
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // pojok kanan bawah
                .padding(16.dp) // jarak dari tepi
            ,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = "Simpan Data"
            )
        }
    }

    if (alertState.value.isVisible) {
        AlertDialog(
            onDismissRequest = {
                AlertHelper.dismiss(alertState)
            },
            title = {
                Text(alertState.value.type.title)
            },
            text = {
                Text(alertState.value.message)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        AlertHelper.dismiss(alertState)
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewbooksEditUI() {
//    DelcomTheme {
//        booksEditUI(
//            books = DummyData.getbooksEditData(),
//            onOpen = {}
//        )
//    }
}