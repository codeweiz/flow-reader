package io.flowreader.app

import android.app.Application
import io.flowreader.data.RepositoryProvider
import io.flowreader.domain.repository.BookRepository

class FlowReaderApp : Application() {

    lateinit var repository: BookRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = RepositoryProvider.bookRepository(this)
    }
}
