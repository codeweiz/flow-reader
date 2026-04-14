package io.flowreader.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val fileUri: String,
    val coverUrl: String? = null,
    val lastReadChapterIndex: Int = 0,
    val lastReadCharIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
