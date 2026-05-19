package com.local.autobook.ui.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionGuideScreen(
    onOpenAccessibilitySettings: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "\u6743\u9650\u5f15\u5bfc")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "\u65e0\u969c\u788d\u670d\u52a1")
                Text(text = "\u7528\u4e8e\u8bc6\u522b\u652f\u4ed8\u5b8c\u6210\u9875\u9762\u5e76\u751f\u6210\u5019\u9009\u6d41\u6c34\u3002")
                Button(onClick = onOpenAccessibilitySettings) {
                    Text(text = "\u6253\u5f00\u65e0\u969c\u788d\u8bbe\u7f6e")
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "\u901a\u77e5\u76d1\u542c")
                Text(text = "\u4f5c\u4e3a\u8865\u5145\u8bc6\u522b\u6765\u6e90\uff0c\u52a9\u529b\u6d41\u6c34\u53bb\u91cd\u3002")
                Button(onClick = onOpenNotificationSettings) {
                    Text(text = "\u6253\u5f00\u901a\u77e5\u76d1\u542c")
                }
            }
        }
        Button(onClick = onBack) {
            Text(text = "\u8fd4\u56de")
        }
    }
}
