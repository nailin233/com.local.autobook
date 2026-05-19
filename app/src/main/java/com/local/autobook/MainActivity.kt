package com.local.autobook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.local.autobook.ui.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as AutoBookApp
        setContent {
            MaterialTheme {
                Surface {
                    AppRoot(app = app)
                }
            }
        }
    }
}

@Composable
private fun AppRoot(app: AutoBookApp) {
    AppNav(repository = app.container.transactionRepository)
}
