package com.diegowmenezes.pagoapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.diegowmenezes.pagoapp.ui.dashboard.DashboardScreen
import com.diegowmenezes.pagoapp.ui.newtransaction.NewTransactionScreen
import com.diegowmenezes.pagoapp.ui.reports.ReportsScreen
import com.diegowmenezes.pagoapp.ui.transactions.TransactionListScreen

private data class BottomNavItem(
    val screen: PagoAppScreen,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(PagoAppScreen.Dashboard, "Dashboard", Icons.Filled.Dashboard),
    BottomNavItem(PagoAppScreen.TransactionList, "Transacoes", Icons.Filled.ReceiptLong),
    BottomNavItem(PagoAppScreen.NewTransaction, "Nova", Icons.Filled.AddCircle),
    BottomNavItem(PagoAppScreen.Reports, "Relatorios", Icons.Filled.BarChart)
)

@Composable
fun PagoAppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.screen.route
                            } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PagoAppScreen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(PagoAppScreen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToNewTransaction = {
                        navController.navigate(PagoAppScreen.NewTransaction.route)
                    },
                    onNavigateToTransactions = {
                        navController.navigate(PagoAppScreen.TransactionList.route)
                    }
                )
            }
            composable(PagoAppScreen.TransactionList.route) {
                TransactionListScreen()
            }
            composable(PagoAppScreen.NewTransaction.route) {
                NewTransactionScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(PagoAppScreen.Reports.route) {
                ReportsScreen()
            }
        }
    }
}