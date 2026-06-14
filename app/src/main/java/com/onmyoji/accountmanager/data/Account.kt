package com.onmyoji.accountmanager.data

data class Account(
    val id: Long = 0,
    val accountType: String,
    val server: String,
    val phoneNumber: String,
    val password: String,
    val shikigami: String,
    val level: Int,
    val status: String,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
