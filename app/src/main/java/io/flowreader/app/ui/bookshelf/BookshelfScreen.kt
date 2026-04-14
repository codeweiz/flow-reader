package io.flowreader.app.ui.bookshelf

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.flowreader.app.LocalRepository
import io.flowreader.app.ui.reader.ReaderScreen
import io.flowreader.domain.model.Book
import io.flowreader.domain.usecase.GetBooksUseCase

class BookshelfScreen(
    private val onImportClick: () -> Unit
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository = LocalRepository.current
        var books by remember { mutableStateOf<List<Book>>(emptyList()) }

        LaunchedEffect(Unit) {
            books = GetBooksUseCase(repository).invoke()
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("书架") })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onImportClick) {
                    Icon(Icons.Default.Add, contentDescription = "导入书籍")
                }
            }
        ) { paddingValues ->
            BookshelfContent(
                books = books,
                onImportClick = onImportClick,
                onBookClick = { book -> navigator.push(ReaderScreen(bookId = book.id)) },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun BookshelfContent(
    books: List<Book>,
    onImportClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (books.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无书籍，点击右下角导入 TXT", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn {
                items(books, key = { it.id }) { book ->
                    BookItem(
                        book = book,
                        onClick = { onBookClick(book) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookItem(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = book.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "作者：${book.author}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (book.lastReadChapterIndex > 0 || book.lastReadCharIndex > 0) {
                Text(
                    text = "已读到第 ${book.lastReadChapterIndex + 1} 章",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
