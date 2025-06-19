package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.egorpoprotskiy.eyeofwages.R
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.egorpoprotskiy.eyeofwages.navigation.MonthNavHost
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination


object MonthEntryDestination: NavigationDestination {
    override val route = "month_entry"
    override val titleHead = R.string.vvod_dannyh
}
@Composable
fun MonthEntryScreen(
    navigateToMonthDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
//        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        InputText(stringResource(R.string.oklad))
        InputText(stringResource(R.string.norma))
        InputText(stringResource(R.string.rab_time))
        InputText(stringResource(R.string.noch_time))
        InputText(stringResource(R.string.prazd_time))
        InputText(stringResource(R.string.premia))

//        InputText(stringResource(R.string.visluga))
//        InputText(stringResource(R.string.prikaz))
//        InputText(stringResource(R.string.prikaz_den))
//        InputText(stringResource(R.string.prikaz_noch))

        //Выбор выслуги лет
        val vislugaOptions = listOf("0", "5", "10", "15")
        var selectedVislugaOption by remember { mutableStateOf(vislugaOptions[0]) }
        Column {
            Text(
                text = stringResource(R.string.visluga),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                vislugaOptions.forEach { text ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedVislugaOption),
                            onClick = { selectedVislugaOption = text }
                        )
                        Text(text = text)
                    }
                }
            }
        }
        //Выбор приказа
        val options = listOf("день", "ночь")
        var selectedOption by remember { mutableStateOf(options[0]) }
        Column {
            Text(
                text = stringResource(R.string.prikaz),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { text ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = { selectedOption = text }
                        )
                        Text(text = text)
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            onClick = navigateToMonthDetails
        ) {
            Text(text = stringResource(R.string.raschet))
        }
    }
}

@Composable
fun InputText(numberDescription: String) {
    var numberText by remember { mutableStateOf("") }
    OutlinedTextField(
        value = numberText,
        onValueChange = {it},
        label = { Text(text = numberDescription)},
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun EyeOfWagesApp(navController: NavHostController = rememberNavController()) {
    MonthNavHost(navController = navController)
}

@Preview
@Composable
fun MonthEntreScreenPreview() {
    MonthEntryScreen(
        navigateToMonthDetails = {}
    )
}