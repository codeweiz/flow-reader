package io.flowreader.feature.reader.domain

import io.flowreader.core.data.repository.BookRepository
import io.flowreader.core.model.Chapter

class GetBookContentUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(bookId: String): List<Chapter> = repository.getChapters(bookId)
}
