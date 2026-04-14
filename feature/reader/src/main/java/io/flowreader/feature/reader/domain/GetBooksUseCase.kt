package io.flowreader.feature.reader.domain

import io.flowreader.core.data.repository.BookRepository
import io.flowreader.core.model.Book

class GetBooksUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(): List<Book> = repository.getBooks()
}
