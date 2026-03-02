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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    val uiStateBook by BookViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        uiStateBook.BookAction = BookActionUIState.Loading
    }

    // ✅ Semua parameter sesuai nama field backend
    fun onSave(
        context: Context,
        title: String,
        description: String,
        genre: String,
        mainCharacter: String,
        author: String,
        file: Uri
    ) {
        isLoading = true

        val titleBody = title.toRequestBodyText()
        val descriptionBody = description.toRequestBodyText()
        val genreBody = genre.toRequestBodyText()
        val mainCharacterBody = mainCharacter.toRequestBodyText()
        val authorBody = author.toRequestBodyText() // ✅ bukan karakterUtama

        val filePart = uriToMultipart(context, file, "file")

        BookViewModel.postBook(
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
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.SUCCESS,
                    message = state.message
                )
                RouteHelper.to(navController, ConstHelper.RouteNames.books.path, true)
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

    if (isLoading) {
        LoadingUI()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBarComponent(
            navController = navController,
            title = "Tambah Data",
            showBackButton = true,
        )
        Box(modifier = Modifier.weight(1f)) {
            booksAddUI(onSave = ::onSave)
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun booksAddUI(
    onSave: (Context, String, String, String, String, String, Uri) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }

    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataTitle by remember { mutableStateOf("") }           // ✅ bukan dataNama
    var dataDescription by remember { mutableStateOf("") }    // ✅ bukan dataDeskripsi
    var dataGenre by remember { mutableStateOf("") }
    var dataMainCharacter by remember { mutableStateOf("") }  // ✅ bukan dataKarakterUtama
    var dataAuthor by remember { mutableStateOf("") }         // ✅ bukan dataPenulis
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val descriptionFocus = remember { FocusRequester() }
    val genreFocus = remember { FocusRequester() }
    val mainCharacterFocus = remember { FocusRequester() }
    val authorFocus = remember { FocusRequester() }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> dataFile = uri }

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
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
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
                    Text("Pilih Gambar", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tap untuk mengganti gambar", style = MaterialTheme.typography.bodySmall)
        }

        // Title
        OutlinedTextField(
            value = dataTitle,
            onValueChange = { dataTitle = it },
            label = { Text("Judul Buku", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { descriptionFocus.requestFocus() }),
        )

        // Description
        OutlinedTextField(
            value = dataDescription,
            onValueChange = { dataDescription = it },
            label = { Text("Deskripsi", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().height(120.dp).focusRequester(descriptionFocus),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { genreFocus.requestFocus() }),
            maxLines = 5, minLines = 3
        )

        // Genre
        OutlinedTextField(
            value = dataGenre,
            onValueChange = { dataGenre = it },
            label = { Text("Genre", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(genreFocus),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { mainCharacterFocus.requestFocus() }),
        )

        // Main Character
        OutlinedTextField(
            value = dataMainCharacter,
            onValueChange = { dataMainCharacter = it },
            label = { Text("Karakter Utama", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(mainCharacterFocus),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { authorFocus.requestFocus() }),
        )

        // Author
        OutlinedTextField(
            value = dataAuthor,
            onValueChange = { dataAuthor = it },
            label = { Text("Penulis", color = MaterialTheme.colorScheme.onPrimaryContainer) },
            modifier = Modifier.fillMaxWidth().focusRequester(authorFocus),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        )

        Spacer(modifier = Modifier.height(64.dp))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                if (dataFile == null) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Gambar tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataTitle.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Judul tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataDescription.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Deskripsi tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataGenre.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Genre tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataMainCharacter.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Karakter utama tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataAuthor.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Penulis tidak boleh kosong!")
                    return@FloatingActionButton
                }
                onSave(context, dataTitle, dataDescription, dataGenre, dataMainCharacter, dataAuthor, dataFile!!)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
            confirmButton = {
                TextButton(onClick = { AlertHelper.dismiss(alertState) }) { Text("OK") }
            }
        )
    }
}
