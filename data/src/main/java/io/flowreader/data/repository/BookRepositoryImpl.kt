package io.flowreader.data.repository

import io.flowreader.data.local.db.AppDatabase
import io.flowreader.data.local.db.BookEntity
import io.flowreader.data.local.db.ChapterEntity
import io.flowreader.data.local.parser.TxtParser
import io.flowreader.domain.model.Book
import io.flowreader.domain.model.Chapter
import io.flowreader.domain.repository.BookRepository
import java.util.UUID

class BookRepositoryImpl(
    private val database: AppDatabase
) : BookRepository {

    override suspend fun getBooks(): List<Book> {
        return database.bookDao().getAll().map { it.toDomain() }
    }

    override suspend fun getBook(bookId: String): Book? {
        return database.bookDao().getById(bookId)?.toDomain()
    }

    override suspend fun importBook(fileUri: String, title: String, content: String): Result<Book> {
        return runCatching {
            val bookId = UUID.randomUUID().toString()
            val chapters = TxtParser.parseChapters(content)

            val bookEntity = BookEntity(
                id = bookId,
                title = title,
                author = "未知",
                fileUri = fileUri
            )
            database.bookDao().insert(bookEntity)

            var charOffset = 0L
            val chapterEntities = chapters.mapIndexed { index, (chapterTitle, chapterContent) ->
                val start = charOffset
                val end = charOffset + chapterContent.length
                charOffset = end
                ChapterEntity(
                    bookId = bookId,
                    index = index,
                    title = chapterTitle,
                    startCharIndex = start,
                    endCharIndex = end,
                    content = chapterContent
                )
            }
            database.chapterDao().insertAll(chapterEntities)

            bookEntity.toDomain()
        }
    }

    override suspend fun getChapters(bookId: String): List<Chapter> {
        return database.chapterDao().getByBookId(bookId).map { it.toDomain() }
    }

    override suspend fun saveProgress(bookId: String, chapterIndex: Int, charIndex: Int) {
        database.bookDao().updateProgress(
            bookId = bookId,
            chapterIndex = chapterIndex,
            charIndex = charIndex,
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun BookEntity.toDomain() = Book(
        id = id,
        title = title,
        author = author,
        coverUrl = coverUrl,
        lastReadChapterIndex = lastReadChapterIndex,
        lastReadCharIndex = lastReadCharIndex,
        updatedAt = updatedAt
    )

    private fun ChapterEntity.toDomain() = Chapter(
        bookId = bookId,
        index = index,
        title = title,
        startCharIndex = startCharIndex,
        endCharIndex = endCharIndex,
        content = content
    )
}
