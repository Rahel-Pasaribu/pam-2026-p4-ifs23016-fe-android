package org.delcom.pam_p4_ifs23016.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage<T>(
    val status: String,
    val message: String,
    val data: T? = null
)