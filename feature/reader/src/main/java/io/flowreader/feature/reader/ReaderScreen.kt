package io.flowreader.feature.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.flowreader.core.data.repository.BookRepository
import io.flowreader.core.model.Book
import io.flowreader.core.model.Chapter
import io.flowreader.feature.reader.domain.GetBookContentUseCase
import io.flowreader.feature.reader.domain.GetBooksUseCase
import io.flowreader.feature.reader.domain.SaveProgressUseCase
import kotlinx.coroutines.launch

class ReaderScreen(
    private val repository: BookRepository,
    private val bookId: String
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var book by remember { mutableStateOf<Book?>(null) }
        var chapters by remember { mutableStateOf<List<Chapter>>(emptyList()) }
        var currentIndex by remember { mutableIntStateOf(0) }
        val scrollState = rememberScrollState()

        LaunchedEffect(bookId) {
            book = GetBooksUseCase(repository).invoke().find { it.id == bookId }
            chapters = GetBookContentUseCase(repository).invoke(bookId)
            currentIndex = book?.lastReadChapterIndex?.coerceIn(0, (chapters.size - 1).coerceAtLeast(0)) ?: 0
        }

        DisposableEffect(Unit) {
            onDispose {
                val idx = currentIndex
                val charIndex = scrollState.value
                scope.launch {
                    SaveProgressUseCase(repository).invoke(bookId, idx, charIndex)
                }
            }
        }

        ReaderContent(
            title = book?.title ?: "阅读",
            chapters = chapters,
            currentIndex = currentIndex,
            onPrev = {
                if (currentIndex > 0) {
                    currentIndex--
                    scope.launch { scrollState.scrollTo(0) }
                }
            },
            onNext = {
                if (currentIndex < chapters.size - 1) {
                    currentIndex++
                    scope.launch { scrollState.scrollTo(0) }
                }
            },
            onBack = { navigator.pop() },
            scrollState = scrollState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderContent(
    title: String,
    chapters: List<Chapter>,
    currentIndex: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit = {},
    scrollState: androidx.compose.foundation.ScrollState = rememberScrollState()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPrev,
                    enabled = currentIndex > 0
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "上一章")
                }
                Text(
                    text = if (chapters.isNotEmpty()) "${currentIndex + 1} / ${chapters.size}" else "",
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = onNext,
                    enabled = currentIndex < chapters.size - 1
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "下一章")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (chapters.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val chapter = chapters[currentIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = chapter.content,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}
