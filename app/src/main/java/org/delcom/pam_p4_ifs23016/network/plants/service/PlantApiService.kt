package org.delcom.pam_p4_ifs23016.network.plants.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23016.network.data.ResponseMessage
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlant
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlantAdd
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponsePlants
import org.delcom.pam_p4_ifs23016.network.plants.data.ResponseProfile
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PlantApiService {
    // Ambil profile developer
    @GET("profile")
    suspend fun getProfile(): ResponseMessage<ResponseProfile?>

    // Ambil semua data tumbuhan
    @GET("plants")
    suspend fun getAllPlants(
        @Query("search") search: String? = null
    ): ResponseMessage<ResponsePlants?>

    // Tambah data tumbuhan
    @Multipart
    @POST("/plants")
    suspend fun postPlant(
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("manfaat") manfaat: RequestBody,
        @Part("efekSamping") efekSamping: RequestBody,
        @Part file: MultipartBody.Part
    ): ResponseMessage<ResponsePlantAdd?>

    // Ambil data tumbuhan berdasarkan ID
    @GET("plants/{plantId}")
    suspend fun getPlantById(
        @Path("plantId") plantId: String
    ): ResponseMessage<ResponsePlant?>


    // Ubah data tumbuhan
    @Multipart
    @PUT("/plants/{plantId}")
    suspend fun putPlant(
        @Path("plantId") plantId: String,
        @Part("nama") nama: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part("manfaat") manfaat: RequestBody,
        @Part("efekSamping") efekSamping: RequestBody,
        @Part file: MultipartBody.Part? = null
    ): ResponseMessage<String?>

    // Hapus data tumbuhan
    @DELETE("plants/{plantId}")
    suspend fun deletePlant(
        @Path("plantId") plantId: String
    ): ResponseMessage<String?>
}