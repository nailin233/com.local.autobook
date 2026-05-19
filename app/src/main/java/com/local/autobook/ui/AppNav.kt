package com.local.autobook.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.local.autobook.data.repository.TransactionRepository
import com.local.autobook.repository.PendingTransactionRepository
import com.local.autobook.ui.edit.TransactionEditScreen
import com.local.autobook.ui.ledger.LedgerListScreen
import com.local.autobook.ui.pending.PendingConfirmScreen
import com.local.autobook.ui.permission.PermissionGuideScreen
import com.local.autobook.ui.permission.PermissionSettingsNavigator

@Composable
fun AppNav(
    repository: TransactionRepository,
    pendingRepository: PendingTransactionRepository,
    startRoute: String = "ledger"
) {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = startRoute) {
        composable("ledger") {
            LedgerListScreen(
                repository = repository,
                onAddClick = { navController.navigate("edit/new") },
                onEditClick = { id -> navController.navigate("edit/$id") },
                onPendingClick = { navController.navigate("pending") },
                onPermissionClick = { navController.navigate("permission") }
            )
        }
        composable("permission") {
            PermissionGuideScreen(
                onOpenAccessibilitySettings = {
                    context.startActivity(
                        android.content.Intent(
                            PermissionSettingsNavigator.accessibilitySettingsAction()
                        )
                    )
                },
                onOpenNotificationSettings = {
                    context.startActivity(
                        android.content.Intent(
                            PermissionSettingsNavigator.notificationListenerSettingsAction()
                        )
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("pending") {
            PendingConfirmScreen(
                pendingStore = pendingRepository,
                transactionStore = repository,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "edit/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { entry ->
            TransactionEditScreen(
                repository = repository,
                transactionId = entry.arguments?.getString("transactionId"),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
