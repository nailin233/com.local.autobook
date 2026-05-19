package com.local.autobook.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.local.autobook.data.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
