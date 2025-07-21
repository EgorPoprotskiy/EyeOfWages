package com.egorpoprotskiy.eyeofwages.month


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.eyeofwages.AppViewModelProvider
import com.egorpoprotskiy.eyeofwages.R
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import kotlinx.coroutines.launch

object MonthEditDestination : NavigationDestination {
    override val route = "month_edit"
    override val titleRes = R.string.edit_raschet
    const val monthIdArg = "monthId"
    val routeWithArgs = "$route/{$monthIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthEditScreen(
    navigateBack: () -> Unit,
    onNavigateUP: () -> Unit,
    viewModel: MonthEditViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MonthTopAppBar(
                title = stringResource(MonthEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUP,
//                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        MonthEntryBody(
            itemUiState = viewModel.monthUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateItem()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MonthEditScreenPreview() {
    MaterialTheme {
        MonthEditScreen(
            navigateBack = {},
            onNavigateUP = {}
        )
    }
}

