package com.local.autobook.ui.ledger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.local.autobook.data.entity.TransactionEntity
import com.local.autobook.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LedgerUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val incomeCents: Long = 0L,
    val expenseCents: Long = 0L
) {
    val incomeText: String = incomeCents.toMoneyText()
    val expenseText: String = expenseCents.toMoneyText()
}

class LedgerViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    val uiState: StateFlow<LedgerUiState> = repository.observeTransactions()
        .map { transactions ->
            LedgerUiState(
                transactions = transactions,
                incomeCents = transactions
                    .filter { it.direction == "INCOME" }
                    .sumOf { it.amountCents },
                expenseCents = transactions
                    .filter { it.direction == "EXPENSE" }
                    .sumOf { it.amountCents }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LedgerUiState())

    fun delete(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    companion object {
        fun factory(repository: TransactionRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LedgerViewModel(repository) as T
                }
            }
    }
}

private fun Long.toMoneyText(): String {
    return "${this / 100}.${(this % 100).toString().padStart(2, '0')}"
}
