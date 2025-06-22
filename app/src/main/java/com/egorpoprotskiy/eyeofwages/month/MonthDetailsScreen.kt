package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.egorpoprotskiy.eyeofwages.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import java.math.BigDecimal
import java.math.RoundingMode

object MonthDetailsDestination : NavigationDestination {
    override val route = "month_details"
    override val titleHead = R.string.detali_rascheta
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthDetailsScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    viewModel: MonthEntryViewModel = viewModel()
) {
    val data = viewModel.inputData
    if (data == null) {
        // Можно показать экран загрузки или заглушку
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Данные не загружены")
        }
        return
    }
    //позволяет TopAppBar прокручивать содержимое(скрываться при прокрутке)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MonthTopAppBar(
                title = stringResource(MonthDetailsDestination.titleHead),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        // Математические вычисления
        val oneChasDenRub = if (data.norma != 0) data.oklad / data.norma else 0.0
        val oneChasNochRub = oneChasDenRub * 0.4

        val rabTime = data.rabTime * oneChasDenRub
        val nochTime = data.nochTime * oneChasNochRub
        val prikazNoch = data.prikazNoch * oneChasDenRub

        val premia = (rabTime + nochTime + prikazNoch) * (data.premia / 100.0)
        val prazdTime = data.prazdTime * oneChasDenRub
        val vysluga =
            (rabTime + nochTime + prikazNoch + premia + prazdTime) * (data.visluga / 100.0)

        val base = rabTime + nochTime + prikazNoch + premia + prazdTime + vysluga

        val rayon20 = base * 0.2
        val severn30 = base * 0.3
        val rayon10 = base * 0.1

        val itogBezNdfl = base + rayon20 + severn30 + rayon10
        val ndfl = itogBezNdfl * 0.13
        val itog = itogBezNdfl - ndfl

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MonthDetailsRow(stringResource(R.string.rab_time_rub), round2(rabTime)) }
            item { HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color) }

            item { MonthDetailsRow(stringResource(R.string.noch_time_rub), round2(nochTime)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.premia), round2(premia)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.prazd_time_rub), round2(prazdTime)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.prikaz_rub), round2(prikazNoch)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.rayon_20), round2(rayon20)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.severn_30), round2(severn30)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.rayon_dop_10), round2(rayon10)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.visluga_rub), round2(vysluga)) }
            item {
                HorizontalDivider(thickness = 1.dp, color = DividerDefaults.color)
            }

            item { MonthDetailsRow(stringResource(R.string.otpusk_rub), round2(0.0)) }
            item {
                HorizontalDivider(
                    thickness = 10.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item { MonthDetailsRow(stringResource(R.string.itog), round2(itog)) }
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


@Preview
@Composable
fun MonthDetailsScreenPreview() {
//    MonthDetailsScreen(    )
}