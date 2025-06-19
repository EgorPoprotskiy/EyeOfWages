package com.egorpoprotskiy.eyeofwages.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsDestination
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEntryDestination
import com.egorpoprotskiy.eyeofwages.month.MonthEntryScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEntryViewModel


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
            backStackEntry ->
            val viewModel: MonthEntryViewModel = viewModel(backStackEntry)
            MonthEntryScreen(
                navigateToMonthDetails = { data ->
                    viewModel.setData(data)
                    navController.navigate(MonthDetailsDestination.route)
                }
            )
        }
        composable(
            route = MonthDetailsDestination.route,
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(MonthEntryDestination.route)
            }
            val viewModel: MonthEntryViewModel = viewModel(parentEntry)
            MonthDetailsScreen(
                viewModel = viewModel,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}