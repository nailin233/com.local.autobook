package com.local.autobook.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.local.autobook.data.repository.TransactionRepository

@Composable
fun TransactionEditScreen(
    repository: TransactionRepository,
    transactionId: String?,
    onBack: () -> Unit
) {
    val parsedId = transactionId?.toLongOrNull()
    val viewModel: TransactionEditViewModel = viewModel(
        key = "transaction-edit-${transactionId ?: "new"}",
        factory = TransactionEditViewModel.factory(repository, parsedId)
    )
    val state by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = if (state.id == null) "\u65b0\u589e\u6d41\u6c34" else "\u7f16\u8f91\u6d41\u6c34")
        OutlinedTextField(
            value = state.amount,
            onValueChange = viewModel::updateAmount,
            label = { Text(text = "\u91d1\u989d") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = state.direction == "EXPENSE",
                onClick = { viewModel.updateDirection("EXPENSE") },
                label = { Text(text = "\u652f\u51fa") }
            )
            FilterChip(
                selected = state.direction == "INCOME",
                onClick = { viewModel.updateDirection("INCOME") },
                label = { Text(text = "\u6536\u5165") }
            )
        }
        OutlinedTextField(
            value = state.category,
            onValueChange = viewModel::updateCategory,
            label = { Text(text = "\u5206\u7c7b") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.merchant,
            onValueChange = viewModel::updateMerchant,
            label = { Text(text = "\u5546\u6237") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.note,
            onValueChange = viewModel::updateNote,
            label = { Text(text = "\u5907\u6ce8") },
            modifier = Modifier.fillMaxWidth()
        )
        state.error?.let { Text(text = it) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.save(onBack) }) {
                Text(text = "\u4fdd\u5b58")
            }
            OutlinedButton(onClick = onBack) {
                Text(text = "\u8fd4\u56de")
            }
            if (state.id != null) {
                OutlinedButton(onClick = { showDeleteConfirm = true }) {
                    Text(text = "\u5220\u9664")
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = "\u5220\u9664\u6d41\u6c34") },
            text = { Text(text = "\u786e\u5b9a\u8981\u5220\u9664\u8fd9\u6761\u6d41\u6c34\u5417\uff1f") },
            confirmButton = {
                Button(onClick = { viewModel.delete(onBack) }) {
                    Text(text = "\u5220\u9664")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text(text = "\u53d6\u6d88")
                }
            }
        )
    }
}
