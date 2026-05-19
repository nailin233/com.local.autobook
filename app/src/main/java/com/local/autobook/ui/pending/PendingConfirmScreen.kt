package com.local.autobook.ui.pending

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.local.autobook.data.entity.PendingTransactionEntity
import com.local.autobook.data.repository.TransactionStore
import com.local.autobook.repository.PendingTransactionStore

@Composable
fun PendingConfirmScreen(
    pendingStore: PendingTransactionStore,
    transactionStore: TransactionStore,
    onBack: () -> Unit
) {
    val viewModel: PendingConfirmViewModel = viewModel(
        factory = PendingConfirmViewModel.factory(pendingStore, transactionStore)
    )
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "\u5f85\u786e\u8ba4\u6d41\u6c34", fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = onBack) {
                Text(text = "\u8fd4\u56de")
            }
        }

        if (state.pendingTransactions.isEmpty()) {
            Text(text = "\u6682\u65e0\u5f85\u786e\u8ba4\u6d41\u6c34")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.pendingTransactions, key = { it.id }) { pending ->
                    PendingRow(
                        pending = pending,
                        onConfirm = { viewModel.confirm(pending) },
                        onCancel = { viewModel.cancel(pending) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun PendingRow(
    pending: PendingTransactionEntity,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "${pending.merchant}  ${formatAmount(pending.amountCents, pending.direction)}")
            Text(text = "${pending.category} / ${pending.paymentSource} / ${pending.detectedFrom}")
            if (pending.confidence == "UNCLEAR") {
                Text(text = "\u8bf7\u6838\u5bf9")
            }
            Text(text = pending.rawSummary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onConfirm) {
                    Text(text = "\u786e\u8ba4\u5165\u8d26")
                }
                OutlinedButton(onClick = onCancel) {
                    Text(text = "\u53d6\u6d88")
                }
            }
        }
    }
}

private fun formatAmount(amountCents: Long, direction: String): String {
    val prefix = if (direction == "INCOME") "+" else "-"
    return "$prefix${amountCents / 100}.${(amountCents % 100).toString().padStart(2, '0')}"
}
