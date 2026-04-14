package io.flowreader.data

import android.content.Context
import androidx.room.Room
import io.flowreader.data.local.db.AppDatabase
import io.flowreader.data.repository.BookRepositoryImpl
import io.flowreader.domain.repository.BookRepository

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
