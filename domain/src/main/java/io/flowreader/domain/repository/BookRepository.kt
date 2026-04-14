package io.flowreader.domain.repository

import io.flowreader.domain.model.Book
import io.flowreader.domain.model.Chapter

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun getBook(bookId: String): Book?
    suspend fun importBook(fileUri: String, title: String, content: String): Result<Book>
    suspend fun getChapters(bookId: String): List<Chapter>
    suspend fun saveProgress(bookId: String, chapterIndex: Int, charIndex: Int)
}
