package io.flowreader.core.model

data class Chapter(
    val bookId: String,
    val index: Int,
    val title: String,
    val startCharIndex: Long,
    val endCharIndex: Long,
    val content: String
)
