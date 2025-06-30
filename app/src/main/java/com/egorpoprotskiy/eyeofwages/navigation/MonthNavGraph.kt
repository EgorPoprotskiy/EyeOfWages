package com.egorpoprotskiy.eyeofwages.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsDestination
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEntryDestination
import com.egorpoprotskiy.eyeofwages.month.MonthEntryScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEntryViewModel
import com.egorpoprotskiy.eyeofwages.home.HomeDestination
import com.egorpoprotskiy.eyeofwages.home.HomeScreen
import com.egorpoprotskiy.eyeofwages.month.MonthEditDestination
import com.egorpoprotskiy.eyeofwages.month.MonthEditScreen


@Composable
fun MonthNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
//        startDestination = MonthEntryDestination.route,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        //HomeScreen -> EntryScreen or EditScreen
        composable (route = HomeDestination.route) {
            HomeScreen(
                navigateToMonthEntry = {navController.navigate(MonthEntryDestination.route)},
                navigateToMonthUpdate = {navController.navigate("${MonthDetailsDestination.route}/${it}")}
            )
        }

        //EntryScreen -> DetailsScreen
        composable (route = MonthEntryDestination.route) {
            MonthEntryScreen(
                onNavigateUp = {navController.popBackStack()},
                navigateToMonthDetails = {navController.navigate("${MonthDetailsDestination.route}/${it}")}
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
                //Переход на Домашний экран
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
        //

        //DetailsScreen -> EntryScreen

    }

    //DetailsScreen -> HomeScreen
}