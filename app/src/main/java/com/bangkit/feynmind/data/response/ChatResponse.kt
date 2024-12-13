package com.bangkit.feynmind.data.response

data class ChatResponse(
    val status: String,
    val message: String,
    val data: Any? // Sesuaikan dengan struktur data API
)

