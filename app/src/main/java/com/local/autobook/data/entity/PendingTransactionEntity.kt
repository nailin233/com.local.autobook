package com.local.autobook.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pending_transactions",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["dedupeKey"]),
        Index(value = ["paymentSource"])
    ]
)
data class PendingTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val amountCents: Long,
    val direction: String,
    val category: String,
    val merchant: String,
    val paymentSource: String,
    val detectedFrom: String,
    val occurredAt: Long,
    val confidence: String,
    val rawSummary: String,
    val dedupeKey: String,
    val createdAt: Long
)
