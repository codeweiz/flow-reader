package io.flowreader.domain.usecase

import io.flowreader.domain.model.Book
import io.flowreader.domain.repository.BookRepository

class ImportBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(fileUri: String, title: String, content: String): Result<Book> {
        return repository.importBook(fileUri, title, content)
    }
}
