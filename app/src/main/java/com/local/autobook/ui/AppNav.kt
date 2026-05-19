package com.local.autobook.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.local.autobook.data.repository.TransactionRepository
import com.local.autobook.ui.edit.TransactionEditScreen
import com.local.autobook.ui.ledger.LedgerListScreen

@Composable
fun AppNav(
    repository: TransactionRepository,
    startRoute: String = "ledger"
) {
    val navController: NavHostController = rememberNavController()
    NavHost(navController = navController, startDestination = startRoute) {
        composable("ledger") {
            LedgerListScreen(
                repository = repository,
                onAddClick = { navController.navigate("edit/new") },
                onEditClick = { id -> navController.navigate("edit/$id") }
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
