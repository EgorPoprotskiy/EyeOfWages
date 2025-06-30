package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.egorpoprotskiy.eyeofwages.AppViewModelProvider
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.navigation.MonthNavHost
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


object MonthEntryDestination : NavigationDestination {
    override val route = "month_entry"
    override val titleRes = R.string.vvod_dannyh
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthEntryScreen(
    navigateToMonthDetails: (Month) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: MonthEntryViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    //позволяет TopAppBar прокручивать содержимое(скрываться при прокрутке)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    //Scaffold для создания макета с верхней панелью(topBar)
    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MonthTopAppBar(
                title = stringResource(MonthEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
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
                    navigateToMonthDetails(viewModel.monthUiState.itemDetails.toItem())
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
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(R.dimen.padding_medium))
    ) {
        MonthEntryText(
            itemDetails = itemUiState.itemDetails,
            onItemValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            onClick = onSaveClick,
            enabled = itemUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
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
        modifier = modifier
            //отступы внутри Column
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        InputText(
            itemDetails.oklad,
            onItemValueChange as (String) -> Unit,
            stringResource(R.string.oklad)
        )
        InputText(
            itemDetails.norma,
            onItemValueChange as (String) -> Unit,
            stringResource(R.string.norma)
        )
        InputText(
            itemDetails.rabTime,
            onItemValueChange as (String) -> Unit,
            stringResource(R.string.rab_time_chas)
        )
        InputText(
            itemDetails.nochTime,
            onItemValueChange as (String) -> Unit,
            stringResource(R.string.noch_time_chas)
        )
        InputText(
            itemDetails.prazdTime,
            onItemValueChange as (String) -> Unit,
            stringResource(R.string.prazd_time_chas)
        )
        InputText(
            itemDetails.prikaz,
            onItemValueChange as (String) -> Unit,
            stringResource(R.string.prikaz_chas)
        )
        InputText(
            itemDetails.premia,
            onItemValueChange as (String) -> Unit,
            stringResource(R.string.premia)
        )
        val vislugaOptions = listOf("0", "5", "10", "15")
        Column {
            Text(
                text = stringResource(R.string.visluga_let),
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
                            selected = (text == itemDetails.visluga),
                            onClick = { itemDetails.visluga = text }
                        )
                        Text(text = text)
                    }
                }
            }
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
        value = textFieldValue,
        onValueChange = {
            if (it.text.all { ch -> ch.isDigit() }) {
                textFieldValue = it
                onValueChange(it.text)
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
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                    prikaz = "0",
                    itog = "10000" // Возможно, itog здесь не нужен для ввода, если он вычисляется
                )
            ),
            onItemValueChange = {},
            onSaveClick = {}
        )
    }
}