package org.delcom.pam_p4_ifs23016.network.plants.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.delcom.pam_p4_ifs23016.BuildConfig
import java.util.concurrent.TimeUnit

class PlantAppContainer : IPlantAppContainer {

    // BASE URL
    private val BASE_URL = "https://pam-2026-p4-ifs23016-be.rahel.fun:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }

        connectTimeout(2, TimeUnit.MINUTES)
        readTimeout(2, TimeUnit.MINUTES)
        writeTimeout(2, TimeUnit.MINUTES)
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val retrofitService: PlantApiService by lazy {
        retrofit.create(PlantApiService::class.java)
    }

    override val plantRepository: IPlantRepository by lazy {
        PlantRepository(retrofitService)
    }
}