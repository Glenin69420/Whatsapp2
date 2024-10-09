package com.example.proyecto1

data class Chat(
    val id: String = "",
    val userIds: List<String> = listOf(),
    val timestamp: Long = 0L
)
