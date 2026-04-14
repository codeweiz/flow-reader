package io.flowreader.feature.bookshelf

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.flowreader.core.model.Book

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun BookshelfScreenPreview() {
    val fakeBooks = listOf(
        Book(id = "1", title = "斗破苍穹", author = "天蚕土豆", lastReadChapterIndex = 12, lastReadCharIndex = 3400),
        Book(id = "2", title = "凡人修仙传", author = "忘语", lastReadChapterIndex = 0, lastReadCharIndex = 0),
        Book(id = "3", title = "魔兽世界", author = "我吃西红柿", lastReadChapterIndex = 88, lastReadCharIndex = 1200)
    )
    BookshelfContent(
        books = fakeBooks,
        onImportClick = {},
        onBookClick = {}
    )
}
