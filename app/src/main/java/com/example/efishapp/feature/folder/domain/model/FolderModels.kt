package com.example.efishapp.feature.folder.domain.model

data class Folder(
    val id: String = "",
    val name: String = "",
    val userId: String = "",
    val isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val color: String = ""
)


