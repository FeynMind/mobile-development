package com.bangkit.feynmind.ui.chat

data class ChatMessage(
    val message: String,
    val isUserMessage: Boolean,
    val fileUri: String? = null
)
