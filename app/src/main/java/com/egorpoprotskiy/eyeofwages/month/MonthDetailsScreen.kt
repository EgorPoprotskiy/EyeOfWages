package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
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
//    onNavigateUp: () -> Unit,
//    modifier: Modifier = Modifier,
//    canNavigateBack: Boolean = true,
//    viewModel: MonthEntryViewModel = viewModel()
    navigateToEditMonth: (Int) -> Unit,
    navigateBack: () -> Unit,
    navigateToHomeScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MonthDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
//    val uiState = viewModel.uiState.collectAsState()
    val uiState = viewModel.uiState.collectAsState()
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
            monthDetailsUiState = uiState.value,
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
        )
        // Математические вычисления
//        val oneChasDenRub = if (uiState.norma != 0) data.oklad / data.norma else 0.0
//        val oneChasNochRub = oneChasDenRub * 0.4
//
//        val rabTime = data.rabTime * oneChasDenRub
//        val nochTime = data.nochTime * oneChasNochRub
//        val prikazNoch = data.prikaz * oneChasDenRub
//
//        val premia = (rabTime + nochTime + prikazNoch) * (data.premia / 100.0)
//        val prazdTime = data.prazdTime * oneChasDenRub
////        val vysluga = (rabTime + nochTime + prikazNoch + premia + prazdTime) * (data.visluga / 100.0)
//        val vysluga = (data.oklad) * (data.visluga / 100.0)
//
//        val base = rabTime + nochTime + prikazNoch + premia + prazdTime + vysluga
//
//        val rayon20 = base * 0.2
//        val severn30 = base * 0.3
//        val rayon10 = base * 0.1
//
//        val itogBezNdfl = base + rayon20 + severn30 + rayon10
//        val ndfl = itogBezNdfl * 0.13
//        data.itog = itogBezNdfl - ndfl

//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(10.dp)
//                .background(MaterialTheme.colorScheme.background),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            item { MonthDetailsRow(stringResource(R.string.rab_time_rub), round2(rabTime)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.noch_time_rub), round2(nochTime)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.premia_rub), round2(premia)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.prazd_time_rub), round2(prazdTime)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.prikaz_rub), round2(prikazNoch)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.rayon_20), round2(rayon20)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.severn_30), round2(severn30)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.rayon_dop_10), round2(rayon10)) }
//            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
//
//            item { MonthDetailsRow(stringResource(R.string.visluga_rub), round2(vysluga)) }
////            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }
////
////            item { MonthDetailsRow(stringResource(R.string.otpusk_rub), round2(0.0)) }
//            item { HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.primary) }
//
//            item { MonthDetailsRow(stringResource(R.string.itog), round2(data.itog)) }
//        }
    }
}

@Composable
private fun MonthDetailsBody(
    monthDetailsUiState: MonthDetailsUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        MonthDetails(
            month = monthDetailsUiState.monthDetails.toItem(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun MonthDetails(
    month: Month,
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
            val oneChasDenRub = if (month.norma != 0) month.oklad / month.norma else 0.0
            val oneChasNochRub = oneChasDenRub * 0.4

            val rabTime = month.rabTime * oneChasDenRub
            val nochTime = month.nochTime * oneChasNochRub
            val prikazNoch = month.prikaz * oneChasDenRub

            val premia = (rabTime + nochTime + prikazNoch) * (month.premia / 100.0)
            val prazdTime = month.prazdTime * oneChasDenRub
//        val vysluga = (rabTime + nochTime + prikazNoch + premia + prazdTime) * (data.visluga / 100.0)
            val vysluga = (month.oklad) * (month.visluga / 100.0)

            val base = rabTime + nochTime + prikazNoch + premia + prazdTime + vysluga

            val rayon20 = base * 0.2
            val severn30 = base * 0.3
            val rayon10 = base * 0.1

            val itogBezNdfl = base + rayon20 + severn30 + rayon10
            val ndfl = itogBezNdfl * 0.13
            month.itog = itogBezNdfl - ndfl
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rab_time_rub),
                monthDetails = rabTime
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.noch_time_rub),
                monthDetails = nochTime
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.premia_rub),
                monthDetails = premia
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.prazd_time_rub),
                monthDetails = prazdTime
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.prikaz_rub),
                monthDetails = prikazNoch
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rayon_20),
                monthDetails = rayon20
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.severn_30),
                monthDetails = severn30
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rayon_dop_10),
                monthDetails = rayon10
            )
            HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.visluga_rub),
                monthDetails = round2(vysluga)
            )
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.primary)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.itog),
                monthDetails = month.itog
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
        MonthDetailsBody(
            MonthDetailsUiState(
                outOfStock = true,
                monthDetails = MonthDetails(
                    id = 1,
                    oklad = "50000.0",
                    rabTime = "160.0",
                    nochTime = "20.0",
                    prikaz = "10.0",
                    premia = "5.0",
                    prazdTime = "8.0",
                    norma = "160.0",
                    visluga = "10.0",
                    itog = "60000.0"
                ))
            )
    }
}