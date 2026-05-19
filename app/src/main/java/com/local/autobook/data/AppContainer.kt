package com.local.autobook.data

import android.content.Context
import androidx.room.Room
import com.local.autobook.data.repository.TransactionRepository

class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "autobook.db"
    ).build()

    val transactionRepository = TransactionRepository(database.transactionDao())
}
