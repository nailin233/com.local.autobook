package com.local.autobook.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.local.autobook.data.entity.TransactionEntity
import com.local.autobook.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionEditUiState(
    val id: Long? = null,
    val original: TransactionEntity? = null,
    val amount: String = "",
    val direction: String = "EXPENSE",
    val category: String = "\u9910\u996e",
    val merchant: String = "",
    val note: String = "",
    val error: String? = null
)

class TransactionEditViewModel(
    private val repository: TransactionRepository,
    private val transactionId: Long?
) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionEditUiState())
    val uiState: StateFlow<TransactionEditUiState> = _uiState

    init {
        if (transactionId != null) {
            viewModelScope.launch {
                repository.getById(transactionId)?.let { entity ->
                    _uiState.value = TransactionEditUiState(
                        id = entity.id,
                        original = entity,
                        amount = entity.amountCents.toAmountText(),
                        direction = entity.direction,
                        category = entity.category,
                        merchant = entity.merchant,
                        note = entity.note
                    )
                }
            }
        }
    }

    fun updateAmount(value: String) = _uiState.update { it.copy(amount = value, error = null) }

    fun updateDirection(value: String) = _uiState.update { it.copy(direction = value) }

    fun updateCategory(value: String) = _uiState.update { it.copy(category = value) }

    fun updateMerchant(value: String) = _uiState.update { it.copy(merchant = value) }

    fun updateNote(value: String) = _uiState.update { it.copy(note = value) }

    fun save(onSaved: () -> Unit) {
        val current = _uiState.value
        val amountCents = current.amount.toAmountCents()
        if (amountCents == null || amountCents <= 0) {
            _uiState.update { it.copy(error = "\u8bf7\u8f93\u5165\u6709\u6548\u91d1\u989d") }
            return
        }
        val now = System.currentTimeMillis()
        val original = current.original
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = current.id ?: 0L,
                amountCents = amountCents,
                direction = current.direction,
                category = current.category.ifBlank { "\u672a\u5206\u7c7b" },
                merchant = current.merchant.ifBlank { "\u672a\u586b\u5199" },
                paymentSource = "MANUAL",
                detectedFrom = "MANUAL",
                occurredAt = original?.occurredAt ?: now,
                note = current.note,
                createdAt = original?.createdAt ?: now,
                updatedAt = now
            )
            if (current.id == null) {
                repository.insert(entity)
            } else {
                repository.update(entity)
            }
            onSaved()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val original = _uiState.value.original ?: return
        viewModelScope.launch {
            repository.delete(original)
            onDeleted()
        }
    }

    companion object {
        fun factory(
            repository: TransactionRepository,
            transactionId: Long?
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TransactionEditViewModel(repository, transactionId) as T
                }
            }
    }
}

private fun Long.toAmountText(): String {
    return "${this / 100}.${(this % 100).toString().padStart(2, '0')}"
}

private fun String.toAmountCents(): Long? {
    val normalized = trim()
    if (normalized.isBlank()) return null
    val parts = normalized.split(".")
    if (parts.size > 2) return null
    val yuan = parts[0].toLongOrNull() ?: return null
    val cents = parts.getOrNull(1)
        ?.padEnd(2, '0')
        ?.take(2)
        ?.toLongOrNull()
        ?: 0L
    return yuan * 100 + cents
}
