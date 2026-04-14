package io.flowreader.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookId: String,
    val index: Int,
    val title: String,
    val startCharIndex: Long,
    val endCharIndex: Long,
    val content: String
)
