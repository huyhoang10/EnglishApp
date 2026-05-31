package com.example.efishapp.feature.folder.domain.model

data class Folder(
    val id: String = "",
    val name: String,
    val description: String = "",
    val topicType: Topic = Topic.CUSTOM,
    val vocabularyCount: Int = 0,
    val colorHex: Long = topicType.colorHex,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val ownerId: String = ""
)
