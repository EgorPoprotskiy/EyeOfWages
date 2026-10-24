package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.egorpoprotskiy.eyeofwages.R
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.eyeofwages.AppViewModelProvider
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import kotlinx.coroutines.launch


object MonthEntryDestination : NavigationDestination {
    override val route = "month_entry"
    override val titleRes = R.string.vvod_dannyh
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: MonthEntryViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    //позволяет TopAppBar прокручивать содержимое(скрываться при прокрутке)
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    //Scaffold для создания макета с верхней панелью(topBar)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MonthTopAppBar(
                title = stringResource(MonthEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
//                scrollBehavior = scrollBehavior
            )
        }
        //innerPadding - отступы, которые нужно применить к содержимому Scaffold
    ) { innerPadding ->
        MonthEntryBody(
            itemUiState = viewModel.monthUiState,
            onItemValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveItem()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        )
    }
}

@Composable
fun MonthEntryBody(
    itemUiState: MonthUiState,
    onItemValueChange: (MonthDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large))

    ) {
        MonthEntryText(
            itemDetails = itemUiState.itemDetails,
            onItemValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            onClick = onSaveClick,
            enabled = itemUiState.isEntryValid,
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(text = stringResource(R.string.raschet))
        }
    }
}

@Composable
fun MonthEntryText(
    itemDetails: MonthDetails,
    modifier: Modifier = Modifier,
    onItemValueChange: (MonthDetails) -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EntrySection(title = "Период расчёта") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InputYearAndMonth(
                    value = itemDetails.yearName,
                    onValueChange = { onItemValueChange(itemDetails.copy(yearName = it)) },
                    label = stringResource(R.string.yearName),
                    modifier = Modifier.weight(1f)
                )
                InputYearAndMonth(
                    value = itemDetails.monthName,
                    onValueChange = { onItemValueChange(itemDetails.copy(monthName = it)) },
                    label = stringResource(R.string.monthName),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        EntrySection(title = "Основные данные") {
            InputText(itemDetails.oklad, { onItemValueChange(itemDetails.copy(oklad = it)) }, stringResource(R.string.oklad))
            InputText(itemDetails.norma, { onItemValueChange(itemDetails.copy(norma = it)) }, stringResource(R.string.norma))
            InputText(itemDetails.rabTime, { onItemValueChange(itemDetails.copy(rabTime = it)) }, stringResource(R.string.rab_time_chas))
        }

        EntrySection(title = "Доплаты и надбавки") {
            InputText(itemDetails.nochTime, { onItemValueChange(itemDetails.copy(nochTime = it)) }, stringResource(R.string.noch_time_chas))
            InputText(itemDetails.prazdTime, { onItemValueChange(itemDetails.copy(prazdTime = it)) }, stringResource(R.string.prazd_time_chas))
            InputText(itemDetails.prikazDen, { onItemValueChange(itemDetails.copy(prikazDen = it)) }, stringResource(R.string.prikaz_den))
            InputText(itemDetails.prikazNoch, { onItemValueChange(itemDetails.copy(prikazNoch = it)) }, stringResource(R.string.prikaz_noch))
            InputText(itemDetails.premia, { onItemValueChange(itemDetails.copy(premia = it)) }, stringResource(R.string.premia))
            InputText(itemDetails.otherPayments, { onItemValueChange(itemDetails.copy(otherPayments = it)) }, stringResource(R.string.other_payments))
        }

        EntrySection(title = "Больничный и отпуск") {
            InputText(itemDetails.bolnichniy, { onItemValueChange(itemDetails.copy(bolnichniy = it)) }, stringResource(R.string.bolnichniy))
            InputText(itemDetails.otpuskDays, { onItemValueChange(itemDetails.copy(otpuskDays = it)) }, stringResource(R.string.otpuskDays_detail))
            InputText(itemDetails.otpuskPay, { onItemValueChange(itemDetails.copy(otpuskPay = it)) }, stringResource(R.string.otpuskPay_detail))
        }

        EntrySection(title = stringResource(R.string.visluga_let)) {
            val vislugaOptions = listOf("0", "5", "10", "15")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                vislugaOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onItemValueChange(itemDetails.copy(visluga = option)) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == itemDetails.visluga,
                            onClick = { onItemValueChange(itemDetails.copy(visluga = option)) }
                        )
                        Text(text = option, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun EntrySection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            content()
        }
    }
}

@Composable
fun InputText(
    value: String,
    onValueChange: (String) -> Unit = {},
    label: String
) {
    //9 Для фокуса и управления курсором в текстовом поле
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value))
    }
    var isFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = {
            // Проверка, что можно ввести только одну десятичную точку.
            val newValue = if (it.count { ch -> ch == '.' } <= 1) {
                it
            } else {
                value
            }
            //Разрешение на ввод и числа и точки.
            if (newValue.all { ch -> ch.isDigit() || ch == '.' }) {
                onValueChange(newValue)
            }
        },
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !isFocused) {
                    textFieldValue =
                        textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
                }
                isFocused = focusState.isFocused
            },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun InputYearAndMonth(
    value: String,
    onValueChange: (String) -> Unit = {},
    label: String,
    modifier: Modifier = Modifier
) {
    //9 Для фокуса и управления курсором в текстовом поле
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value))
    }
    var isFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = {
//                textFieldValue = it
                onValueChange(it)
        },
        label = { Text(text = label) },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !isFocused) {
                    textFieldValue =
                        textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
                }
                isFocused = focusState.isFocused
            },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview
@Composable
fun MonthEntreScreenPreview() {
    MaterialTheme { // Обертка MaterialTheme должна быть здесь
        MonthEntryBody(
            itemUiState = MonthUiState(
                MonthDetails( // Убедитесь, что конструктор соответствует определению MonthDetails
                    oklad = "10000",
                    norma = "160",
                    rabTime = "160",
                    nochTime = "0",
                    prazdTime = "0",
                    premia = "40",
                    visluga = "0",
                    prikazDen = "0",
                    prikazNoch = "0",
                    otherPayments = "0",
                    itog = "10000", // Возможно, itog здесь не нужен для ввода, если он вычисляется
                    bolnichniy = "777",
                    otpuskPay = "777.0"

                )
            ),
            onItemValueChange = {},
            onSaveClick = {}
        )
    }
}