package io.flowreader.domain.usecase

import io.flowreader.domain.model.Chapter
import io.flowreader.domain.repository.BookRepository

class GetBookContentUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(bookId: String): List<Chapter> = repository.getChapters(bookId)
}
