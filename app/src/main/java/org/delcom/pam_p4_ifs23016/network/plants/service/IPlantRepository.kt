package org.delcom.pam_p4_ifs23016.network.plants.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlant
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlantAdd
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlants
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponseProfile

interface IPlantRepository {
    // Ambil profile developer
    suspend fun getProfile(): ResponseMessage<ResponseProfile?>

    // Ambil semua data tumbuhan
    suspend fun getAllPlants(
        search: String? = null
    ): ResponseMessage<ResponsePlants?>

    // Tambah data tumbuhan
    suspend fun postPlant(
        nama: RequestBody,
        deskripsi: RequestBody,
        manfaat: RequestBody,
        efekSamping: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponsePlantAdd?>

    // Ambil data tumbuhan berdasarkan ID
    suspend fun getPlantById(
        plantId: String
    ): ResponseMessage<ResponsePlant?>


    // Ubah data tumbuhan
    suspend fun putPlant(
        plantId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        manfaat: RequestBody,
        efekSamping: RequestBody,
        file: MultipartBody.Part? = null
    ): ResponseMessage<String?>

    // Hapus data tumbuhan
    suspend fun deletePlant(
        plantId: String
    ): ResponseMessage<String?>
}