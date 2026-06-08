package com.example.efishapp.feature.folder.domain.model

import com.google.firebase.firestore.PropertyName

data class Folder(
    val id: String = "",
    val name: String = "",
    val userId: String = "",
    @get:PropertyName("isStarred")
    @set:PropertyName("isStarred")
    var isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val color: String = ""
)


