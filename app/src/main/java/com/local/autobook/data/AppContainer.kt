package com.local.autobook.data

import android.content.Context
import androidx.room.Room
import com.local.autobook.detection.AlipayPaymentDetector
import com.local.autobook.detection.DetectedPayment
import com.local.autobook.detection.NotificationPaymentProcessor
import com.local.autobook.detection.PaymentDeduplicator
import com.local.autobook.detection.WeChatPaymentDetector
import com.local.autobook.domain.CreatePendingFromDetectionUseCase
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
    private val createPendingFromDetectionUseCase = CreatePendingFromDetectionUseCase(
        pendingStore = pendingTransactionRepository,
        deduplicator = PaymentDeduplicator()
    )

    val notificationPaymentProcessor = NotificationPaymentProcessor(
        createPending = { detected: DetectedPayment ->
            createPendingFromDetectionUseCase.create(detected)
        },
        detectors = listOf(
            WeChatPaymentDetector(),
            AlipayPaymentDetector()
        )
    )
}
