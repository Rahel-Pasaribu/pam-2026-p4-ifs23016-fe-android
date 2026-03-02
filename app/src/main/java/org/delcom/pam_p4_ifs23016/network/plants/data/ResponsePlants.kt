package org.delcom.pam_p4_ifs23016.network.plants.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponsePlants (
    val plants: List<ResponsePlantData>
)

@Serializable
data class ResponsePlant (
    val plant: ResponsePlantData
)

@Serializable
data class ResponsePlantAdd (
    val plantId: String
)

@Serializable
data class ResponsePlantData(
    val id: String,
    val nama: String,
    val deskripsi: String,
    val manfaat: String,
    val efekSamping: String,
    val createdAt: String,
    val updatedAt: String
)