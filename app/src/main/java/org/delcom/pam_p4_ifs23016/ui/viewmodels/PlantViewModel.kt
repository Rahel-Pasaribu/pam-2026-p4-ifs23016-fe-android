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
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlantData
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponseProfile
import org.delcom.pam_p4_ifs23016.network.plants.service.IPlantRepository
import javax.inject.Inject

sealed interface ProfileUIState {
    data class Success(val data: ResponseProfile) : ProfileUIState
    data class Error(val message: String) : ProfileUIState
    object Loading : ProfileUIState
}

sealed interface PlantsUIState {
    data class Success(val data: List<ResponsePlantData>) : PlantsUIState
    data class Error(val message: String) : PlantsUIState
    object Loading : PlantsUIState
}

sealed interface PlantUIState {
    data class Success(val data: ResponsePlantData) : PlantUIState
    data class Error(val message: String) : PlantUIState
    object Loading : PlantUIState
}

sealed interface PlantActionUIState {
    data class Success(val message: String) : PlantActionUIState
    data class Error(val message: String) : PlantActionUIState
    object Loading : PlantActionUIState
}

data class UIStatePlant(
    val profile: ProfileUIState = ProfileUIState.Loading,
    val plants: PlantsUIState = PlantsUIState.Loading,
    var plant: PlantUIState = PlantUIState.Loading,
    var plantAction: PlantActionUIState = PlantActionUIState.Loading
)

@HiltViewModel
@Keep
class PlantViewModel @Inject constructor(
    private val repository: IPlantRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UIStatePlant())
    val uiState = _uiState.asStateFlow()

    fun getProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    profile = ProfileUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.getProfile()
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            ProfileUIState.Success(it.data!!)
                        } else {
                            ProfileUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        ProfileUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    profile = tmpState
                )
            }
        }
    }

    fun getAllPlants(search: String? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    plants = PlantsUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.getAllPlants(search)
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            PlantsUIState.Success(it.data!!.plants)
                        } else {
                            PlantsUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        PlantsUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    plants = tmpState
                )
            }
        }
    }

    fun postPlant(
        nama: RequestBody,
        deskripsi: RequestBody,
        manfaat: RequestBody,
        efekSamping: RequestBody,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    plantAction = PlantActionUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.postPlant(
                        nama = nama,
                        deskripsi = deskripsi,
                        manfaat = manfaat,
                        efekSamping = efekSamping,
                        file = file
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            PlantActionUIState.Success(it.data!!.plantId)
                        } else {
                            PlantActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        PlantActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    plantAction = tmpState
                )
            }
        }
    }

    fun getPlantById(plantId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    plant = PlantUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.getPlantById(plantId)
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            PlantUIState.Success(it.data!!.plant)
                        } else {
                            PlantUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        PlantUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    plant = tmpState
                )
            }
        }
    }

    fun putPlant(
        plantId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        manfaat: RequestBody,
        efekSamping: RequestBody,
        file: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    plantAction = PlantActionUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.putPlant(
                        plantId = plantId,
                        nama = nama,
                        deskripsi = deskripsi,
                        manfaat = manfaat,
                        efekSamping = efekSamping,
                        file = file
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            PlantActionUIState.Success(it.message)
                        } else {
                            PlantActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        PlantActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    plantAction = tmpState
                )
            }
        }
    }

    fun deletePlant(plantId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    plantAction = PlantActionUIState.Loading
                )
            }
            _uiState.update { it ->
                val tmpState = runCatching {
                    repository.deletePlant(
                        plantId = plantId
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            PlantActionUIState.Success(it.message)
                        } else {
                            PlantActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        PlantActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                it.copy(
                    plantAction = tmpState
                )
            }
        }
    }
}