package com.egorpoprotskiy.eyeofwages.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.egorpoprotskiy.eyeofwages.AppViewModelProvider
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsDestination
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEntryDestination
import com.egorpoprotskiy.eyeofwages.month.MonthEntryScreen
import com.egorpoprotskiy.eyeofwages.home.HomeDestination
import com.egorpoprotskiy.eyeofwages.home.HomeScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEditDestination
import com.egorpoprotskiy.eyeofwages.month.MonthEditScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEditViewModel


@Composable
fun MonthNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        //HomeScreen -> EntryScreen
        composable (route = HomeDestination.route) {
            HomeScreen(
                navigateToMonthEntry = {navController.navigate(MonthEntryDestination.route)},
                navigateToMonthUpdate = {navController.navigate("${MonthDetailsDestination.route}/${it}")}
            )
        }

        //EntryScreen -> HomeScreen
        composable (route = MonthEntryDestination.route) {
            MonthEntryScreen(
                onNavigateUp = {navController.navigateUp()},
                navigateBack = {navController.popBackStack()}
            )
        }

        //DetailsScreen -> EditScreen
        composable (
            route = MonthDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(MonthDetailsDestination.monthIdArgs) {
                type = NavType.IntType
            })
        ) {
            MonthDetailsScreen(
                navigateToEditMonth = {navController.navigate("${MonthEditDestination.route}/$it")},
                navigateBack = { navController.navigateUp() },
            )
        }
        composable(
            route = MonthEditDestination.routeWithArgs,
            arguments = listOf(navArgument(MonthEditDestination.monthIdArg) {
                type = NavType.IntType
            })
        ) {
            MonthEditScreen(
                navigateBack = {navController.popBackStack()},
                onNavigateUP = {navController.navigateUp()}
            )
        }
    }
}