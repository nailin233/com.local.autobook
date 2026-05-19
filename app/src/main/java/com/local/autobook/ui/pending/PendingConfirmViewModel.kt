package com.local.autobook.ui.pending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.data.repository.TransactionStore
import com.local.autobook.domain.CancelPendingTransactionUseCase
import com.local.autobook.domain.ConfirmPendingTransactionUseCase
import com.local.autobook.repository.PendingTransactionStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PendingConfirmUiState(
    val pendingTransactions: List<PendingTransactionEntity> = emptyList()
)

class PendingConfirmViewModel(
    private val pendingStore: PendingTransactionStore,
    transactionStore: TransactionStore
) : ViewModel() {
    private val confirmUseCase = ConfirmPendingTransactionUseCase(
        transactionStore = transactionStore,
        pendingStore = pendingStore
    )
    private val cancelUseCase = CancelPendingTransactionUseCase(pendingStore)

    val uiState: StateFlow<PendingConfirmUiState> = pendingStore.observePendingTransactions()
        .map { PendingConfirmUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PendingConfirmUiState())

    fun confirm(pending: PendingTransactionEntity) {
        viewModelScope.launch {
            confirmUseCase.confirm(pending)
        }
    }

    fun cancel(pending: PendingTransactionEntity) {
        viewModelScope.launch {
            cancelUseCase.cancel(pending)
        }
    }

    companion object {
        fun factory(
            pendingStore: PendingTransactionStore,
            transactionStore: TransactionStore
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PendingConfirmViewModel(pendingStore, transactionStore) as T
                }
            }
    }
}
