package com.egorpoprotskiy.eyeofwages.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsDestination
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEntryDestination
import com.egorpoprotskiy.eyeofwages.month.MonthEntryScreen


@Composable
fun MonthNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MonthEntryDestination.route,
        modifier = modifier
    ) {
        composable(route = MonthEntryDestination.route) {
            MonthEntryScreen(
                navigateToMonthDetails = { navController.navigate("${MonthDetailsDestination.route}" +
//                        "/${it}" +
                        "") }
            )
        }
        composable(
            route = MonthDetailsDestination.route,
        ) {
            MonthDetailsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}