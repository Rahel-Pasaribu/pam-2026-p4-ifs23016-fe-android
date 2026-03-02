package org.delcom.pam_p4_ifs23016.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23016.R
import org.delcom.pam_p4_ifs23016.helper.ConstHelper
import org.delcom.pam_p4_ifs23016.helper.RouteHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlantData
import org.delcom.pam_p4_ifs23016.ui.components.BottomDialog
import org.delcom.pam_p4_ifs23016.ui.components.BottomDialogType
import org.delcom.pam_p4_ifs23016.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23016.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23016.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23016.ui.components.TopAppBarMenuItem
import org.delcom.pam_p4_ifs23016.ui.theme.DelcomTheme
import org.delcom.pam_p4_ifs23016.ui.viewmodels.PlantActionUIState
import org.delcom.pam_p4_ifs23016.ui.viewmodels.PlantUIState
import org.delcom.pam_p4_ifs23016.ui.viewmodels.PlantViewModel

@Composable
fun PlantsDetailScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    plantViewModel: PlantViewModel,
    plantId: String
) {
    // Ambil data dari viewmodel
    val uiStatePlant by plantViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var isConfirmDelete by remember { mutableStateOf(false) }

    // Muat data
    var plant by remember { mutableStateOf<ResponsePlantData?>(null) }

    // Dapatkan tumbuhan berdasarkan ID
    LaunchedEffect(Unit) {
        isLoading = true
        // Reset status plant action
        uiStatePlant.plantAction = PlantActionUIState.Loading
        uiStatePlant.plant = PlantUIState.Loading
        plantViewModel.getPlantById(plantId)
    }

    // Picu ulang ketika data tumbuhan berubah
    LaunchedEffect(uiStatePlant.plant) {
        if(uiStatePlant.plant !is PlantUIState.Loading){
            if(uiStatePlant.plant is PlantUIState.Success){
                plant = (uiStatePlant.plant as PlantUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
            }
        }
    }

    fun onDelete(){
        isLoading = true
        plantViewModel.deletePlant(plantId)
    }

    LaunchedEffect(uiStatePlant.plantAction) {
        when (val state = uiStatePlant.plantAction) {
            is PlantActionUIState.Success -> {
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.SUCCESS,
                    message = state.message
                )
                RouteHelper.to(
                    navController,
                    ConstHelper.RouteNames.Plants.path,
                    true
                )
                uiStatePlant.plant = PlantUIState.Loading
                isLoading = false
            }
            is PlantActionUIState.Error -> {
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
    if(isLoading || plant == null){
        LoadingUI()
        return
    }

    // Menu item details
    val detailMenuItems = listOf(
        TopAppBarMenuItem(
            text = "Ubah Data",
            icon = Icons.Filled.Edit,
            route = null,
            onClick = {
                RouteHelper.to(
                    navController,
                    ConstHelper.RouteNames.PlantsEdit.path
                        .replace("{plantId}", plant!!.id),
                )
            }
        ),
        TopAppBarMenuItem(
            text = "Hapus Data",
            icon = Icons.Filled.Delete,
            route = null,
            onClick = {
                isConfirmDelete = true
            }
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    )
    {
        // Top App Bar
        TopAppBarComponent(
            navController = navController,
            title = plant!!.nama,
            showBackButton = true,
            customMenuItems = detailMenuItems
        )
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            // Content UI
            PlantsDetailUI(
                plant = plant!!
            )
            // Bottom Dialog to Confirmation Delete
            BottomDialog(
                type = BottomDialogType.ERROR,
                show = isConfirmDelete,
                onDismiss = { isConfirmDelete = false },
                title = "Konfirmasi Hapus Data",
                message = "Apakah Anda yakin ingin menghapus data ini?",
                confirmText = "Ya, Hapus",
                onConfirm = {
                    onDelete()
                },
                cancelText = "Batal",
                destructiveAction = true
            )
        }
        // Bottom Nav
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun PlantsDetailUI(
    plant: ResponsePlantData
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    )
    {
        // Gambar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        )
        {
            AsyncImage(
                model = ToolsHelper.getPlantImageUrl(plant.id),
                contentDescription = plant.nama,
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = plant.nama,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Deskripsi
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Deskripsi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                Text(
                    text = plant.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

        }

        // Manfaat
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Manfaat",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                Text(
                    text = plant.manfaat,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

        }

        // Efek Samping
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Efek Samping",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                Text(
                    text = plant.efekSamping,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewPlantsDetailUI() {
    DelcomTheme {
//        PlantsDetailUI(
//            plant = DummyData.getPlantsData()[0]
//        )
    }
}