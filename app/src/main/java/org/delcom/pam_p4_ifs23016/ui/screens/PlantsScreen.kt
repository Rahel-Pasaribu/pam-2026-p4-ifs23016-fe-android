package org.delcom.pam_p4_ifs23016.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23016.R
import org.delcom.pam_p4_ifs23016.helper.ConstHelper
import org.delcom.pam_p4_ifs23016.helper.RouteHelper
import org.delcom.pam_p4_ifs23016.helper.ToolsHelper
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlantData
import org.delcom.pam_p4_ifs23016.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23016.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23016.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23016.ui.viewmodels.PlantViewModel
import org.delcom.pam_p4_ifs23016.ui.viewmodels.PlantsUIState

@Composable
fun PlantsScreen(
    navController: NavHostController,
    plantViewModel: PlantViewModel
) {
    // Ambil data dari viewmodel
    val uiStatePlant by plantViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember {
        mutableStateOf(TextFieldValue(""))
    }

    // Muat data
    var plants by remember { mutableStateOf<List<ResponsePlantData>>(emptyList()) }

    fun fetchPlantsData(){
        isLoading = true
        plantViewModel.getAllPlants(searchQuery.text)
    }

    // Picu pengambilan data plants
    LaunchedEffect(Unit) {
        fetchPlantsData()
    }

    // Picu ketika terjadi perubahan data plants
    LaunchedEffect(uiStatePlant.plants){
        if(uiStatePlant.plants !is PlantsUIState.Loading){
            isLoading = false

            plants = if(uiStatePlant.plants is PlantsUIState.Success) {
                (uiStatePlant.plants as PlantsUIState.Success).data
            }else{
                emptyList()
            }
        }
    }

    // Tampilkan halaman loading
    if(isLoading){
        LoadingUI()
        return
    }

    fun onOpen(plantId: String) {
        RouteHelper.to(
            navController = navController,
            destination = "plants/${plantId}"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBarComponent(
            navController = navController,
            title = "Plants", showBackButton = false,
            withSearch = true,
            searchQuery = searchQuery,
            onSearchQueryChange = { query ->
                searchQuery = query
            },
            onSearchAction = {
                fetchPlantsData()
            }
        )
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            PlantsUI(
                plants = plants,
                onOpen = ::onOpen
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
            {
                // Floating Action Button
                FloatingActionButton(
                    onClick = {
                        RouteHelper.to(
                            navController,
                            ConstHelper.RouteNames
                                .PlantsAdd
                                .path
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // pojok kanan bawah
                        .padding(16.dp) // jarak dari tepi
                    ,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Tumbuhan"
                    )
                }
            }
        }
        // Bottom Nav
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun PlantsUI(
    plants: List<ResponsePlantData>,
    onOpen: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(plants) { plant ->
            PlantItemUI(
                plant,
                onOpen
            )
        }
    }

    if(plants.isEmpty()){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = "Tidak ada data!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun PlantItemUI(
    plant: ResponsePlantData,
    onOpen: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onOpen(plant.id)
            },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = ToolsHelper.getPlantImageUrl(plant.id),
                contentDescription = plant.nama,
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                modifier = Modifier
                    .size(70.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = plant.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = plant.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewPlantsUI() {
//    DelcomTheme {
//        PlantsUI(
//            plants = DummyData.getPlantsData(),
//            onOpen = {}
//        )
//    }
}