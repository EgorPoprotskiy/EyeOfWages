package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
        ) {
            //Для запоминания состояния полей ввода
            var oklad by remember { mutableStateOf("") }
            var norma by remember { mutableStateOf("") }
            var rabTime by remember { mutableStateOf("") }
            var nochTime by remember { mutableStateOf("") }
            var prazdTime by remember { mutableStateOf("") }
            var premia by remember { mutableStateOf("") }
            var visluga by remember { mutableStateOf("") }
            var prikazDen by remember { mutableStateOf("") }
            var prikazNoch by remember { mutableStateOf("") }

            InputText(oklad, onValueChange = {oklad = it }, stringResource(R.string.oklad))
            InputText(norma, onValueChange = {norma = it },stringResource(R.string.norma))
            InputText(rabTime, onValueChange = {rabTime = it },stringResource(R.string.rab_time))
            InputText(nochTime, onValueChange = {nochTime= it },stringResource(R.string.noch_time))
            InputText(prazdTime, onValueChange = {prazdTime = it },stringResource(R.string.prazd_time))
            InputText(premia, onValueChange = {premia = it },stringResource(R.string.premia))

//        InputText(stringResource(R.string.visluga))
//        InputText(stringResource(R.string.prikaz))
        InputText(prikazDen, onValueChange = {prikazDen = it },stringResource(R.string.prikaz_den))
        InputText(prikazNoch, onValueChange = {prikazNoch = it },stringResource(R.string.prikaz_noch))

            //Выбор выслуги лет
            val vislugaOptions = listOf("0", "5", "10", "15")
//            var selectedVislugaOption by remember { mutableStateOf(vislugaOptions[0]) }
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
                                selected = (text == visluga),
                                onClick = { visluga = text }
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
                    //собирает введенные данные в объект Month и далее в переменную data
                    val data = Month(
                        oklad = oklad.toIntOrNull() ?: 0,
                        norma = norma.toIntOrNull() ?: 0,
                        rabTime = rabTime.toIntOrNull() ?: 0,
                        nochTime = nochTime.toIntOrNull() ?: 0,
                        prazdTime = prazdTime.toIntOrNull() ?: 0,
                        premia = premia.toIntOrNull() ?: 0,
                        visluga = visluga.toIntOrNull() ?: 0,
                        prikazDen = prikazDen.toIntOrNull() ?: 0,
                        prikazNoch = prikazNoch.toIntOrNull() ?: 0
                    )
                    //передает данные в viewModel
//                    viewModel.setData(data)
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
//    var numberText by remember { mutableStateOf(inputData) }
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.all { ch -> ch.isDigit() })
                onValueChange(it)
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
                        imageVector = Icons.Filled.ArrowBack,
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