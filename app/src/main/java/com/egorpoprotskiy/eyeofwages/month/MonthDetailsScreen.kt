package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import com.egorpoprotskiy.eyeofwages.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.eyeofwages.AppViewModelProvider
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.data.MonthCalculateData
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

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
    modifier: Modifier = Modifier,
    viewModel: MonthDetailsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState = viewModel.uiState.collectAsState()
    // 20
    val calcState by viewModel.calculateData.collectAsState()
    val totalSdzBase by viewModel.totalSdzBase.collectAsState()
//    val coroutineScope = rememberCoroutineScope()        //позволяет TopAppBar прокручивать содержимое(скрываться при прокрутке)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
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
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_raschet)
                )
            }
        }, modifier = modifier
    ) { innerPadding ->
        MonthDetailsBody(
            month = uiState.value.monthDetails.toItem(),
            calculated = calcState,
            // Сумма зараблтка за год. Сразу же с функцией округления до 2-х знаков после запятой.
            totalSdzBase = BigDecimal(totalSdzBase).setScale(2, RoundingMode.HALF_UP).toDouble(),
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
    month: Month,
    calculated: MonthCalculateData,
    totalSdzBase: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        MonthDetails(
            month = month,
            calculated = calculated,
            totalSdzBase = totalSdzBase,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@Composable
fun MonthDetails(
    month: Month,
    calculated: MonthCalculateData,
    totalSdzBase: Double,
    modifier: Modifier = Modifier
) {
    //переменная, которая хранит состояние expanded для кнопки свернуть/равернуть
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
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
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.noch_time_rub),
                monthDetails = calculated.nochTimeRub
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.premia_rub),
                monthDetails = calculated.premiaRub
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.prazd_time_rub),
                monthDetails = calculated.prazdTimeRub
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.prikaz_den_rub),
                monthDetails = calculated.prikazDenRub
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.prikaz_noch_rub),
                monthDetails = calculated.prikazNochRub
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rayon_20),
                monthDetails = calculated.rayon20
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.severn_30),
                monthDetails = calculated.severn30
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.rayon_dop_10),
                monthDetails = calculated.rayon10
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.visluga_rub),
                monthDetails = calculated.vislugaRub
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.other_payments),
                monthDetails = calculated.otherPaymentsRub
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.itog_bez_ndfl),
                monthDetails = calculated.itogBezNdfl
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.bolnichniy_detail),
                monthDetails = calculated.bolnichniy
            )
            HorizontalDivider(thickness = 1.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.total_accrual_12_months),
                monthDetails = totalSdzBase
            )
            HorizontalDivider(thickness = 7.dp)
            MonthDetailsRow(
                labelDetails = stringResource(R.string.otpuskPay_detail),
//                monthDetails = month.otpuskPay
                monthDetails = calculated.otpuskPay
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ИТОГО К ВЫПЛАТЕ",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = calculated.itog?.let(::formatRubles) ?: "—",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                MonthItemButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded }
                )
            }
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.padding_medium)
                )
            ) {
                MonthDetailsRow(
                    labelDetails = stringResource(R.string.aliments25),
                    monthDetails = calculated.aliments25
                )
                MonthDetailsRow(
                    labelDetails = stringResource(R.string.aliments75),
                    monthDetails = calculated.aliments75
                )
            }
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
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = labelDetails,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            modifier = Modifier.wrapContentSize(),
            text = monthDetails?.let(::formatRubles) ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatRubles(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    formatter.currency = Currency.getInstance("RUB")
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 2
    return formatter.format(value)
}

@Composable
private fun MonthItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = if (expanded) "Скрыть дополнительные удержания" else "Показать дополнительные удержания",
            style = MaterialTheme.typography.labelLarge
        )
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

//Превью светлой темы
@Preview(showBackground = true)
@Composable
fun MonthDetailsScreenLightPreview() {
    com.egorpoprotskiy.eyeofwages.ui.theme.EyeOfWagesTheme(darkTheme = false){
        MonthDetailsBody(
            month = Month(
                id = 1,
                monthName = 1,
                yearName = 2023,
                oklad = 50000.0,
                norma = 160,
                rabTime = 160,
                nochTime = 0,
                prazdTime = 0,
                premia = 5000.0,
                visluga = 0,
                prikazDen = 0,
                prikazNoch = 0,
                itog = 55000.0,
                itogBezNdfl = 777.0,
                bolnichniy = 777.0,
                otpuskDays = 777,
                otpuskPay = 777.0,
                otherPayments = 50000.0
            ),
            calculated = MonthCalculateData(
                rabTimeRub = 50000.0,
                nochTimeRub = 0.0,
                premiaRub = 5000.0,
                prazdTimeRub = 0.0,
                prikazNochRub = 0.0,
                rayon20 = 10000.0,
                severn30 = 15000.0,
                rayon10 = 5000.0,
                vislugaRub = 2000.0,
                otherPaymentsRub = 50000.0,
                itog = 80000.0,
            ),
            totalSdzBase = 777.0,
        )
    }
}
//Превью темной  темы
@Preview(showBackground = true)
@Composable
fun MonthDetailsScreenDarkPreview() {
    com.egorpoprotskiy.eyeofwages.ui.theme.EyeOfWagesTheme(darkTheme = true){
        MonthDetailsBody(
            month = Month(
                id = 1,
                monthName = 1,
                yearName = 2023,
                oklad = 50000.0,
                norma = 160,
                rabTime = 160,
                nochTime = 0,
                prazdTime = 0,
                premia = 5000.0,
                visluga = 0,
                prikazDen = 0,
                prikazNoch = 0,
                itog = 55000.0,
                itogBezNdfl = 777.0,
                bolnichniy = 777.0,
                otpuskDays = 777,
                otpuskPay = 777.0,
                otherPayments = 50000.0
            ),
            calculated = MonthCalculateData(
                rabTimeRub = 50000.0,
                nochTimeRub = 0.0,
                premiaRub = 5000.0,
                prazdTimeRub = 0.0,
                prikazNochRub = 0.0,
                rayon20 = 10000.0,
                severn30 = 15000.0,
                rayon10 = 5000.0,
                vislugaRub = 2000.0,
                otherPaymentsRub = 50000.0,
                itog = 80000.0,
                itogBezNdfl = 10000.0
            ),
            totalSdzBase = 777.0,
        )
    }
}