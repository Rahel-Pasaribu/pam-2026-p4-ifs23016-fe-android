package org.delcom.pam_p4_ifs23016.helper

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object ToolsHelper {

    // BASE URL
    private const val BASE_URL = "https://pam-2026-p4-ifs23016-be.rahel.fun:8080/"

    fun getPlantImageUrl(plantId: String): String {
        return "${BASE_URL}plants/$plantId/image"
    }

    fun getBookImageUrl(bookId: String): String {
        return "${BASE_URL}books/$bookId/image"
    }

    fun getProfilePhotoUrl(): String {
        return "${BASE_URL}profile/photo"
    }

    fun String.toRequestBodyText(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun uriToMultipart(
        context: Context,
        uri: Uri,
        partName: String
    ): MultipartBody.Part {
        val file = uriToFile(context, uri)

        val requestFile = file
            .asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            partName,
            file.name,
            requestFile
        )
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val file = File.createTempFile("upload", ".tmp", context.cacheDir)

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file
    }
}