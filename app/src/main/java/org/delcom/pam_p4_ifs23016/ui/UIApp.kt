package org.delcom.pam_p4_ifs23016.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.delcom.pam_p4_ifs23016.helper.ConstHelper
import org.delcom.pam_p4_ifs23016.ui.components.CustomSnackbar
import org.delcom.pam_p4_ifs23016.ui.screens.HomeScreen
import org.delcom.pam_p4_ifs23016.ui.screens.PlantsAddScreen
import org.delcom.pam_p4_ifs23016.ui.screens.PlantsDetailScreen
import org.delcom.pam_p4_ifs23016.ui.screens.PlantsEditScreen
import org.delcom.pam_p4_ifs23016.ui.screens.PlantsScreen
import org.delcom.pam_p4_ifs23016.ui.screens.booksAddScreen
import org.delcom.pam_p4_ifs23016.ui.screens.booksDetailScreen
import org.delcom.pam_p4_ifs23016.ui.screens.booksEditScreen
import org.delcom.pam_p4_ifs23016.ui.screens.booksScreen
import org.delcom.pam_p4_ifs23016.ui.screens.ProfileScreen
import org.delcom.pam_p4_ifs23016.ui.viewmodels.PlantViewModel
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UIApp(
    navController: NavHostController = rememberNavController(),
    plantViewModel: PlantViewModel,
    BookViewModel: BookViewModel
) {
    // Inisialisasi SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState){ snackbarData ->
            CustomSnackbar(snackbarData, onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() })
        } },
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = ConstHelper.RouteNames.Home.path,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))

        ) {
            // Home
            composable(
                route = ConstHelper.RouteNames.Home.path,
            ) { _ ->
                HomeScreen(
                    navController = navController,
                )
            }

            // Profile
            composable(
                route = ConstHelper.RouteNames.Profile.path,
            ) { _ ->
                ProfileScreen(
                    navController = navController,
                    plantViewModel = plantViewModel
                )
            }

            // Plants
            composable(
                route = ConstHelper.RouteNames.Plants.path,
            ) { _ ->
                PlantsScreen(
                    navController = navController,
                    plantViewModel = plantViewModel
                )
            }

            // Plants Add
            composable(
                route = ConstHelper.RouteNames.PlantsAdd.path,
            ) { _ ->
                PlantsAddScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel
                )
            }

            // Plants Detail
            composable(
                route = ConstHelper.RouteNames.PlantsDetail.path,
                arguments = listOf(
                    navArgument("plantId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""

                PlantsDetailScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel,
                    plantId = plantId
                )
            }

            // Plants Edit
            composable(
                route = ConstHelper.RouteNames.PlantsEdit.path,
                arguments = listOf(
                    navArgument("plantId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""

                PlantsEditScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel,
                    plantId = plantId
                )
            }

            // books
            composable(
                route = "books", // Jika sudah ada di ConstHelper, gunakan: ConstHelper.RouteNames.books.path
            ) { _ ->
                booksScreen( // Pastikan nama ini sesuai dengan fungsi Composable halaman Book kamu
                    navController = navController,
                    BookViewModel = BookViewModel // Menggunakan view model Book yang sudah kita daftarkan di parameter UIApp
                )
            }

            // books Add
            composable(
                route = ConstHelper.RouteNames.booksAdd.path,
            ) { _ ->
                booksAddScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    BookViewModel = BookViewModel
                )
            }

            // books Detail
            composable(
                route = ConstHelper.RouteNames.booksDetail.path,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""

                booksDetailScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    BookViewModel = BookViewModel,
                    bookId = bookId
                )
            }

            // books Edit
            composable(
                route = ConstHelper.RouteNames.booksEdit.path,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""

                booksEditScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    BookViewModel = BookViewModel,
                    bookId = bookId
                )
            }
        }
    }

}