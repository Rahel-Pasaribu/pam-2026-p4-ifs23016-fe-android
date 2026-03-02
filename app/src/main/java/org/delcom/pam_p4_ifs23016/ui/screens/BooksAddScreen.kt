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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import org.delcom.pam_p4_ifs23016.R
import org.delcom.pam_p4_ifs23016.helper.AlertHelper
import org.delcom.pam_p4_ifs23016.helper.AlertState
import org.delcom.pam_p4_ifs23016.helper.AlertType
import org.delcom.pam_p4_ifs23016.helper.ConstHelper
import org.delcom.pam_p4_ifs23016.helper.RouteHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper.toRequestBodyText
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper.uriToMultipart
import org.delcom.pam_p4_ifs23016.network.books.data.ResponseBookData
import org.delcom.pam_p4_ifs23016.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23016.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23016.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookActionUIState
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookViewModel

@Composable
fun booksAddScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    BookViewModel: BookViewModel
) {
    // Ambil data dari viewmodel
    val uiStateBook by BookViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var tmpBook by remember { mutableStateOf<ResponseBookData?>(null) }

    LaunchedEffect(Unit) {
        // Reset status Book action
        uiStateBook.BookAction = BookActionUIState.Loading
    }

    // Simpan data
    fun onSave(
        context: Context,
        nama: String,
        deskripsi: String,
        genre: String,
        karakterUtama: String,
        penulis: String,
        file: Uri
    ) {
        isLoading = true

        tmpBook = ResponseBookData(
            nama = nama,
            deskripsi = deskripsi,
            genre = genre,
            karakterUtama = karakterUtama,
            penulis = penulis,
            id = "",
            createdAt = "",
            updatedAt = ""
        )

        val namaBody = nama.toRequestBodyText()
        val deskripsiBody = deskripsi.toRequestBodyText()
        val genreBody = genre.toRequestBodyText()
        val karakterBody = karakterUtama.toRequestBodyText()
        val penulisBody = karakterUtama.toRequestBodyText()

        val filePart = uriToMultipart(context, file, "file")

        BookViewModel.postBook(
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
                    navController,
                    ConstHelper.RouteNames.books.path,
                    true
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
    if (isLoading) {
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
            title = "Tambah Data",
            showBackButton = true,
        )
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            booksAddUI(
                tmpBook = tmpBook,
                onSave = ::onSave
            )
        }
        // Bottom Nav
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun booksAddUI(
    tmpBook: ResponseBookData?,
    onSave: (
        Context,
        String,
        String,
        String,
        String,
        String,
        Uri
    ) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }

    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataNama by remember { mutableStateOf(tmpBook?.nama ?: "") }
    var dataDeskripsi by remember { mutableStateOf(tmpBook?.deskripsi ?: "") }
    var dataGenre by remember { mutableStateOf(tmpBook?.genre ?: "") }
    var dataKarakterUtama by remember { mutableStateOf(tmpBook?.karakterUtama ?: "") }
    var dataPenulis by remember { mutableStateOf(tmpBook?.penulis ?: "") }
    val context = LocalContext.current

    // Focus manager
    val focusManager = LocalFocusManager.current

    val deskripsiFocus = remember { FocusRequester() }
    val genreFocus = remember { FocusRequester() }
    val karakterFocus = remember { FocusRequester() }
    val penulisFocus = remember { FocusRequester() }


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
                        contentDescription = "Pratinjau Gambar",
                        placeholder = painterResource(R.drawable.img_placeholder),
                        error = painterResource(R.drawable.img_placeholder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Pilih Gambar",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { deskripsiFocus.requestFocus() }
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
                .height(120.dp)
                .focusRequester(deskripsiFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { genreFocus.requestFocus() }
            ),
            maxLines = 5,
            minLines = 3
        )

        //genre
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
                .height(120.dp)
                .focusRequester(genreFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { karakterFocus.requestFocus() }
            ),
            maxLines = 5,
            minLines = 3
        )

        // Karakter Utama
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
                    text = "Karakter Utama",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .focusRequester(karakterFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // menutup keyboard
                }
            ),
            maxLines = 5,
            minLines = 3
        )

        // penulis
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
                .height(120.dp)
                .focusRequester(penulisFocus),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // menutup keyboard
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
                if (dataFile != null) {

                    if(dataNama.isEmpty()) {
                        AlertHelper.show(
                            alertState,
                            AlertType.ERROR,
                            "Nama tidak boleh kosong!"
                        )
                        return@FloatingActionButton
                    }

                    if(dataDeskripsi.isEmpty()) {
                        AlertHelper.show(
                            alertState,
                            AlertType.ERROR,
                            "Deskripsi tidak boleh kosong!"
                        )
                        return@FloatingActionButton
                    }

                    if(dataGenre.isEmpty()) {
                        AlertHelper.show(
                            alertState,
                            AlertType.ERROR,
                            "Informasi genre tidak boleh kosong!"
                        )
                        return@FloatingActionButton
                    }

                    if(dataKarakterUtama.isEmpty()) {
                        AlertHelper.show(
                            alertState,
                            AlertType.ERROR,
                            "Informasi karakter utama tidak boleh kosong!"
                        )
                        return@FloatingActionButton
                    }

                    if(dataPenulis.isEmpty()) {
                        AlertHelper.show(
                            alertState,
                            AlertType.ERROR,
                            "Informasi genre tidak boleh kosong!"
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
                        dataFile!!
                    )
                } else {
                    AlertHelper.show(
                        alertState,
                        AlertType.ERROR,
                        "Gambar tidak boleh kosong!"
                    )
                    return@FloatingActionButton
                }

            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // pojok kanan bawah
                .padding(16.dp) // jarak dari tepi
            ,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
fun PreviewbooksAddUI() {
//    DelcomTheme {
//        booksAddUI(
//            books = DummyData.getbooksAddData(),
//            onOpen = {}
//        )
//    }
}