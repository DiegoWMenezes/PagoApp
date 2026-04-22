package com.diegowmenezes.pagoapp.ui.navigation

sealed class PagoAppScreen(val route: String) {
    data object Dashboard : PagoAppScreen("dashboard")
    data object TransactionList : PagoAppScreen("transactions")
    data object NewTransaction : PagoAppScreen("new_transaction")
    data object Reports : PagoAppScreen("reports")
}