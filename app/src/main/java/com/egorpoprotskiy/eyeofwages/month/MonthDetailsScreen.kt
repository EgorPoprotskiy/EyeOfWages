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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination

object MonthDetailsDestination: NavigationDestination {
    override val route = "month_details"
    override val titleHead = R.string.detali_rascheta
}
@Composable
fun MonthDetailsScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MonthDetailsRow(stringResource(R.string.rab_time), 150000)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.noch_time),100)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.premia),25000)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.prazd_time),5000)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.prikaz),25000)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.rayon_20),20)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.severn_30),30)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.rayon_dop_10),10)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.visluga), 8000)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 1.dp)
        MonthDetailsRow(stringResource(R.string.otpusk), 80000)
        Divider(modifier = Modifier.padding(vertical = 5.dp), thickness = 10.dp)
        MonthDetailsRow(stringResource(R.string.itog), 200000)
    }
}

@Composable
fun MonthDetailsRow(
    labelDetails: String,
    monthDetails: Int,
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
    MonthDetailsScreen(
        modifier = Modifier,
        onNavigateUp = {}
    )
}