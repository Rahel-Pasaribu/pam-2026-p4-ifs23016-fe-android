package org.delcom.pam_p4_ifs23016.network.books.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.delcom.pam_p4_ifs23016.BuildConfig
import java.util.concurrent.TimeUnit


class BookAppContainer : IBookAppContainer {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pam-2026-p4-ifs23016-be.rahel.fun:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val bookApiService = retrofit.create(BookApiService::class.java)

    override val bookRepository: BookRepository by lazy {
        BookRepository(bookApiService)
    }
}