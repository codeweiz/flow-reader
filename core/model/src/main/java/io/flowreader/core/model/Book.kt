package io.flowreader.core.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val lastReadChapterIndex: Int = 0,
    val lastReadCharIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
