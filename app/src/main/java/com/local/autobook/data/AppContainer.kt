package com.local.autobook.data

import android.content.Context
import androidx.room.Room
import com.local.autobook.data.repository.TransactionRepository
import com.local.autobook.repository.PendingTransactionRepository

class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "autobook.db"
    )
        .fallbackToDestructiveMigration(true)
        .build()

    val transactionRepository = TransactionRepository(database.transactionDao())
    val pendingTransactionRepository =
        PendingTransactionRepository(database.pendingTransactionDao())
}
