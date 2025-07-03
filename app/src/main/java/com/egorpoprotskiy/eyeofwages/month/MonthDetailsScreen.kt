package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import com.egorpoprotskiy.eyeofwages.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.eyeofwages.AppViewModelProvider
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthCalculateData
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import java.math.BigDecimal
import java.math.RoundingMode

object MonthDetailsDestination : NavigationDestination {
    override val route = "month_details"
    override val titleRes = R.string.detali_rascheta
    const val monthIdArgs = "monthId"
    val routeWithArgs = "$route/{$monthIdArgs}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDetailsScreen(
    navigateToEditMonth: (Int) -> Unit,
    navigateBack: () -> Unit,
//    navigateToHomeScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MonthDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
//    val uiState = viewModel.uiState.collectAsState()
    val uiState = viewModel.uiState.collectAsState()
    // 20
    val calcState by viewModel.calculateData.collectAsState()
    val coroutineScope = rememberCoroutineScope()        //позволяет TopAppBar прокручивать содержимое(скрываться при прокрутке)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            MonthTopAppBar(
                title = stringResource(MonthDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToEditMonth(uiState.value.monthDetails.id)},
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(
                    end = WindowInsets.safeDrawing.asPaddingValues()
                        .calculateEndPadding(LocalLayoutDirection.current)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_raschet)
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        MonthDetailsBody(
//            monthDetailsUiState = uiState.value,
            month = uiState.value.monthDetails.toItem(),
            calculated = calcState,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun MonthDetailsBody(
//    monthDetailsUiState: MonthDetailsUiState,
    month: Month,
    calculated: MonthCalculateData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        MonthDetails(
//            month = monthDetailsUiState.monthDetails.toItem(),
            month = month,
            calculated = calculated,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun MonthDetails(
    month: Month,
    calculated: MonthCalculateData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.padding_medium)
            )
        ) {
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rab_time_rub),
                monthDetails = calculated.rabTimeRub
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.noch_time_rub),
                monthDetails = calculated.nochTimeRub
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.premia_rub),
                monthDetails = calculated.premiaRub
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.prazd_time_rub),
                monthDetails = calculated.prazdTimeRub
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.prikaz_rub),
                monthDetails = calculated.prikazRub
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rayon_20),
                monthDetails = calculated.rayon20
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.severn_30),
                monthDetails = calculated.severn30
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rayon_dop_10),
                monthDetails = calculated.rayon10
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.visluga_rub),
                monthDetails = calculated.vislugaRub
            )
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.primary)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.itog),
                monthDetails = calculated.itog
            )
        }
    }
}
@Composable
fun MonthDetailsRow(
    labelDetails: String,
    monthDetails: Double?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = labelDetails,
        )
        Text(
            modifier = modifier.wrapContentSize(),
            text = monthDetails?.toString() ?: "-",
        )
    }
}

//Функция округления числа до 2 знаков после запятой
fun round2(value: Double): Double =
    BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()


@Preview(showBackground = true)
@Composable
fun MonthDetailsScreenPreview() {
    MaterialTheme{
//        MonthDetailsBody(
//            MonthDetailsUiState(
//                outOfStock = true,
//                monthDetails = MonthDetails(
//                    id = 1,
//                    oklad = "50000.0",
//                    rabTime = "160.0",
//                    nochTime = "20.0",
//                    prikaz = "10.0",
//                    premia = "5.0",
//                    prazdTime = "8.0",
//                    norma = "160.0",
//                    visluga = "10.0",
//                    itog = "60000.0"
//                ))
//            )
    }
}