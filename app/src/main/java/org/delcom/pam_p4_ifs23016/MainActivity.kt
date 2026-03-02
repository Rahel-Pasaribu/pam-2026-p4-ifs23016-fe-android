package org.delcom.pam_p4_ifs23016

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.delcom.pam_p4_ifs23016.ui.UIApp
import org.delcom.pam_p4_ifs23016.ui.theme.DelcomTheme
import org.delcom.pam_p4_ifs23016.ui.viewmodels.PlantViewModel
import org.delcom.pam_p4_ifs23016.ui.viewmodels.BookViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val plantViewModel: PlantViewModel by viewModels()
    private val BookViewModel: BookViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelcomTheme {
                UIApp(
                    plantViewModel = plantViewModel,
                    BookViewModel = BookViewModel
                )
            }
        }
    }
}