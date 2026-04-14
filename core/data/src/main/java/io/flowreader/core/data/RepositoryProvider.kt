package io.flowreader.core.data

import android.content.Context
import androidx.room.Room
import io.flowreader.core.database.AppDatabase
import io.flowreader.core.data.repository.BookRepository
import io.flowreader.core.data.repository.BookRepositoryImpl

object RepositoryProvider {
    fun bookRepository(context: Context): BookRepository {
        val database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "flow_reader.db"
        ).build()
        return BookRepositoryImpl(database)
    }
}
