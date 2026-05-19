package com.local.autobook.ui.ledger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.local.autobook.data.entity.TransactionEntity
import com.local.autobook.data.repository.TransactionRepository

@Composable
fun LedgerListScreen(
    repository: TransactionRepository,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onPendingClick: () -> Unit,
    onPermissionClick: () -> Unit
) {
    val viewModel: LedgerViewModel = viewModel(
        factory = LedgerViewModel.factory(repository)
    )
    val state by viewModel.uiState.collectAsState()

    LedgerListContent(
        state = state,
        onAddClick = onAddClick,
        onEditClick = onEditClick,
        onPendingClick = onPendingClick,
        onPermissionClick = onPermissionClick
    )
}

@Composable
private fun LedgerListContent(
    state: LedgerUiState,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onPendingClick: () -> Unit,
    onPermissionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "\u968f\u624b\u81ea\u8bb0",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "\u652f\u51fa ${state.expenseText}  \u6536\u5165 ${state.incomeText}")
            }
            Button(onClick = onAddClick) {
                Text(text = "\u65b0\u589e")
            }
        }
        Button(onClick = onPendingClick) {
            Text(text = "\u5f85\u786e\u8ba4")
        }
        Button(onClick = onPermissionClick) {
            Text(text = "\u6743\u9650\u5f15\u5bfc")
        }

        if (state.transactions.isEmpty()) {
            Text(text = "\u6682\u65e0\u6d41\u6c34")
        } else {
            LazyColumn {
                items(state.transactions, key = { it.id }) { item ->
                    TransactionRow(item = item, onEditClick = { onEditClick(item.id) })
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    item: TransactionEntity,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = item.category, fontWeight = FontWeight.SemiBold)
            Text(text = item.merchant)
        }
        Text(text = formatAmount(item.amountCents, item.direction))
    }
}

private fun formatAmount(amountCents: Long, direction: String): String {
    val prefix = if (direction == "INCOME") "+" else "-"
    return "$prefix${amountCents / 100}.${(amountCents % 100).toString().padStart(2, '0')}"
}
