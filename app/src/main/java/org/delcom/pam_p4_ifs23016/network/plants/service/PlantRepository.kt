package org.delcom.pam_p4_ifs23016.network.plants.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.helper.SuspendHelper
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlant
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlantAdd
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlants
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponseProfile

class PlantRepository (private val plantApiService: PlantApiService): IPlantRepository {
    override suspend fun getProfile(): ResponseMessage<ResponseProfile?> {
        return SuspendHelper.safeApiCall {
            plantApiService.getProfile()
        }
    }

    override suspend fun getAllPlants(search: String?): ResponseMessage<ResponsePlants?> {
        return SuspendHelper.safeApiCall {
            plantApiService.getAllPlants(search)
        }
    }

    override suspend fun postPlant(
        nama: RequestBody,
        deskripsi: RequestBody,
        manfaat: RequestBody,
        efekSamping: RequestBody,
        file: MultipartBody.Part
    ): ResponseMessage<ResponsePlantAdd?> {
        return SuspendHelper.safeApiCall {
            plantApiService.postPlant(
                nama = nama,
                deskripsi = deskripsi,
                manfaat = manfaat,
                efekSamping = efekSamping,
                file = file
            )
        }
    }

    override suspend fun getPlantById(plantId: String): ResponseMessage<ResponsePlant?> {
        return SuspendHelper.safeApiCall {
            plantApiService.getPlantById(plantId)
        }
    }

    override suspend fun putPlant(
        plantId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        manfaat: RequestBody,
        efekSamping: RequestBody,
        file: MultipartBody.Part?
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            plantApiService.putPlant(
                plantId = plantId,
                nama = nama,
                deskripsi = deskripsi,
                manfaat = manfaat,
                efekSamping = efekSamping,
                file = file
            )
        }
    }

    override suspend fun deletePlant(plantId: String): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            plantApiService.deletePlant(plantId)
        }
    }
}