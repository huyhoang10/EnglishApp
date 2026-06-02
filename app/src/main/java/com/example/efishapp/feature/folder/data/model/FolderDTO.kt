package com.example.efishapp.feature.folder.data.model

data class FolderDTO(
    val id: String = "",
    val name: String = "",
    val vocabularyCnt: Int = 0,
    val color: String = "",
    val priority: Int = 0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)