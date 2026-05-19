package com.local.autobook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val amountCents: Long,
    val direction: String,
    val category: String,
    val merchant: String,
    val paymentSource: String,
    val detectedFrom: String,
    val occurredAt: Long,
    val note: String,
    val createdAt: Long,
    val updatedAt: Long
)
