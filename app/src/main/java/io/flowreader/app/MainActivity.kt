package io.flowreader.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import io.flowreader.app.ui.bookshelf.BookshelfScreen
import io.flowreader.domain.repository.BookRepository
import io.flowreader.domain.usecase.GetBooksUseCase
import io.flowreader.domain.usecase.GetBookContentUseCase
import io.flowreader.domain.usecase.ImportBookUseCase
import io.flowreader.domain.usecase.SaveProgressUseCase
import kotlinx.coroutines.launch

val LocalRepository = staticCompositionLocalOf<BookRepository> {
    error("Repository not provided")
}

class MainActivity : ComponentActivity() {

    private val repository by lazy { (application as FlowReaderApp).repository }

    private val getBooksUseCase by lazy { GetBooksUseCase(repository) }
    private val importBookUseCase by lazy { ImportBookUseCase(repository) }
    private val getBookContentUseCase by lazy { GetBookContentUseCase(repository) }
    private val saveProgressUseCase by lazy { SaveProgressUseCase(repository) }

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { importBook(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(
                LocalRepository provides repository
            ) {
                Navigator(BookshelfScreen(
                    onImportClick = { openDocumentLauncher.launch(arrayOf("text/plain")) }
                ))
            }
        }
    }

    private fun importBook(uri: Uri) {
        lifecycleScope.launch {
            val content = readTextFromUri(uri)
            val fileName = uri.lastPathSegment ?: "未知"
            val title = fileName.substringBeforeLast(".")
            importBookUseCase(uri.toString(), title, content)
            recreate()
        }
    }

    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().use { it.readText() }
        } ?: ""
    }
}
