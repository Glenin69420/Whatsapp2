package com.example.proyecto1

data class ChatroomModel(
    val chatroomId: String = "",
    val userIds: List<String> = listOf(),
    val lastMessageTimestamp: Long = 0L,
    val lastMessageSenderId: String = "",
    val lastMessage: String = ""
)
