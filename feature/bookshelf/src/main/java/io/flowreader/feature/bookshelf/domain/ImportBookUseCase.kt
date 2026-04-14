package io.flowreader.feature.bookshelf.domain

import io.flowreader.core.data.repository.BookRepository
import io.flowreader.core.model.Book

class ImportBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(fileUri: String, title: String, content: String): Result<Book> {
        return repository.importBook(fileUri, title, content)
    }
}
