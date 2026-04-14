package io.flowreader.feature.reader.domain

import io.flowreader.core.data.repository.BookRepository

class SaveProgressUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(bookId: String, chapterIndex: Int, charIndex: Int) {
        repository.saveProgress(bookId, chapterIndex = chapterIndex, charIndex = charIndex)
    }
}
