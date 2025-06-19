package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Arrangement
import com.egorpoprotskiy.eyeofwages.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import kotlin.div
import kotlin.let
import kotlin.text.toDouble
import kotlin.times

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
    //позволяет TopAppBar прокручивать содержимое(скрываться при прокрутке)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MonthTopAppBar(
                title = stringResource(MonthDetailsDestination.titleHead),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val data = viewModel.inputData

            val oneChasDenRub = data?.norma?.let { data.oklad.div(it.toDouble()) }
            val oneChasNochRub = oneChasDenRub?.times(0.4)
            val rabTime = oneChasDenRub?.let { data.rabTime.times(it) } ?: 0.0
            val nochTime = oneChasNochRub?.times(data.nochTime) ?: 0.0
            val prikazNoch = oneChasDenRub?.let { data.prikazNoch.times(it) } ?: 0.0
            val premia = rabTime.let { nochTime.let { it1 -> prikazNoch.let { it2 -> data?.let { it3 -> (it + it1 + it2) * it3.premia } } } } ?: 0.0
            val prazdTime = oneChasDenRub?.let { data.prazdTime.times(it) } ?: 0.0
            val vysluga = rabTime.let { data?.let { it1 -> (it * it1.visluga) / 100 } } ?: 0.0
            val rayon20 = rabTime.let { nochTime.let { it1 -> prikazNoch.let { it2 -> premia.let { it3 -> prazdTime.let { it4 -> vysluga.let { it5 -> (it + it1 + it2 + it3 + it4 + it5) * 0.2 } } } } } } ?: 0.0
            val severn30 = rabTime.let { nochTime.let { it1 -> prikazNoch.let { it2 -> premia.let { it3 -> prazdTime.let { it4 -> vysluga.let { it5 -> (it + it1 + it2 + it3 + it4 + it5) * 0.3 } } } } } } ?: 0.0
            val rayon10 = rabTime.let { nochTime.let { it1 -> prikazNoch.let { it2 -> premia.let { it3 -> prazdTime.let { it4 -> vysluga.let { it5 -> (it + it1 + it2 + it3 + it4 + it5) * 0.1 } } } } } } ?: 0.0
            val itogBezNdfl = (rabTime + nochTime + prikazNoch + premia + prazdTime + vysluga + rayon20 + severn30 + rayon10)
            val ndfl = itogBezNdfl * 0.13
            val itog = itogBezNdfl - ndfl

            MonthDetailsRow(stringResource(R.string.rab_time), rabTime)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.noch_time), nochTime)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.premia), premia)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.prazd_time), prazdTime)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.prikaz), prikazNoch)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.rayon_20), rayon20)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.severn_30), severn30)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.rayon_dop_10), rayon10)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.visluga), vysluga)
            Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
            MonthDetailsRow(stringResource(R.string.otpusk), 0.0)
            Divider(
                modifier = Modifier.padding(vertical = 5.dp),
                thickness = 10.dp,
                color = MaterialTheme.colorScheme.primary
            )
            MonthDetailsRow(stringResource(R.string.itog), itog)
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
            text = monthDetails.toString(),
        )
    }
}


@Preview
@Composable
fun MonthDetailsScreenPreview() {
//    MonthDetailsScreen(    )
}