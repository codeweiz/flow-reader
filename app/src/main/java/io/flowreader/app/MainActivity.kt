package io.flowreader.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.navigator.Navigator
import io.flowreader.feature.bookshelf.BookshelfScreen
import io.flowreader.feature.bookshelf.domain.ImportBookUseCase
import io.flowreader.feature.reader.ReaderScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val repository by lazy { (application as FlowReaderApp).repository }

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { importBook(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigator(
                BookshelfScreen(
                    repository = repository,
                    onImportClick = { openDocumentLauncher.launch(arrayOf("text/plain")) },
                    onNavigateToReader = { navigator, bookId ->
                        navigator.push(ReaderScreen(repository = repository, bookId = bookId))
                    }
                )
            )
        }
    }

    private fun importBook(uri: Uri) {
        lifecycleScope.launch {
            val content = readTextFromUri(uri)
            val fileName = uri.lastPathSegment ?: "未知"
            val title = fileName.substringBeforeLast(".")
            ImportBookUseCase(repository)(uri.toString(), title, content)
            recreate()
        }
    }

    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)?.use { stream ->
            stream.bufferedReader().use { it.readText() }
        } ?: ""
    }
}
