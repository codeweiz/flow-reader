package io.flowreader.domain.usecase

import io.flowreader.domain.model.Book
import io.flowreader.domain.repository.BookRepository

class GetBooksUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(): List<Book> = repository.getBooks()
}
