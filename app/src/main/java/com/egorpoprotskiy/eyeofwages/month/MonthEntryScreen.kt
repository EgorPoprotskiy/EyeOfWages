package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.navigation.MonthNavHost
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination


object MonthEntryDestination : NavigationDestination {
    override val route = "month_entry"
    override val titleHead = R.string.vvod_dannyh
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthEntryScreen(
    navigateToMonthDetails: (Month) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MonthEntryViewModel = viewModel()
) {
    //позволяет TopAppBar прокручивать содержимое(скрываться при прокрутке)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    //Scaffold для создания макета с верхней панелью(topBar)
    Scaffold(
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MonthTopAppBar(
                title = stringResource(MonthEntryDestination.titleHead),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        }
        //innerPadding - отступы, которые нужно применить к содержимому Scaffold
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                //отступы внутри Column
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            InputText(viewModel.oklad, onValueChange = {viewModel.oklad = it }, stringResource(R.string.oklad))
            InputText(viewModel.norma, onValueChange = {viewModel.norma = it },stringResource(R.string.norma))
            InputText(viewModel.rabTime, onValueChange = {viewModel.rabTime = it },stringResource(R.string.rab_time_chas))
            InputText(viewModel.nochTime, onValueChange = {viewModel.nochTime= it },stringResource(R.string.noch_time_chas))
            InputText(viewModel.prazdTime, onValueChange = {viewModel.prazdTime = it },stringResource(R.string.prazd_time_chas))
            InputText(viewModel.premia, onValueChange = {viewModel.premia = it },stringResource(R.string.premia))
            InputText(viewModel.prikazDen, onValueChange = {viewModel.prikazDen = it },stringResource(R.string.prikaz_den))
            InputText(viewModel.prikazNoch, onValueChange = {viewModel.prikazNoch = it },stringResource(R.string.prikaz_noch))

            //Выбор выслуги лет
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
                                selected = (text == viewModel.visluga),
                                onClick = { viewModel.visluga = text }
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
                onClick = {
                    //передает данные в viewModel
                    val data = viewModel.toMonth()
                    navigateToMonthDetails(data)
                }
            ) {
                Text(text = stringResource(R.string.raschet))
            }
        }
    }
}

@Composable
fun InputText(
    value: String,
    onValueChange: (String) -> Unit,
    label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
//            input ->
//            // Если поле пустое, ставим "0"
//            val newValue = if (input.isEmpty()) "0" else input.filter { it.isDigit() }
//            onValueChange(newValue)
                        },
        label = { Text(text = label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = navigateUp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
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