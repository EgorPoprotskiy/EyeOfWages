package com.egorpoprotskiy.eyeofwages.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.egorpoprotskiy.eyeofwages.AppViewModelProvider
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import com.egorpoprotskiy.eyeofwages.data.Month
import com.egorpoprotskiy.eyeofwages.R
import com.egorpoprotskiy.eyeofwages.data.MonthCalculateData
import com.egorpoprotskiy.eyeofwages.data.monthCalculations
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.set

object HomeDestination: NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToMonthEntry: () -> Unit,
    navigateToMonthUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val homeUiSate by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MonthTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = navigateToMonthEntry,
                shape = androidx.compose.material3.MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.vvod_dannyh)
                )
            }
        }
    ) { innerPadding ->
        HomeBody(
            monthList = homeUiSate.monthList,
            onMonthClick = navigateToMonthUpdate,
            onSwipeDelete = { month ->
                coroutineScope.launch {
                    viewModel.deleteMonth(month)
            }
                            },
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeBody(
    monthList: List<Month>,
    onMonthClick: (Int) -> Unit,
    onSwipeDelete: (Month) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Проверка на пустой список месяцев
        if (monthList.isEmpty()) {
            // Если список пуст, показываем текст
            Text(
                text = "Нет доступных месяцев",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            MonthList(
                monthList = monthList,
                onMonthClick = { onMonthClick(it.id)},
                onSwipeDelete = onSwipeDelete,
                contentPadding = contentPadding,
                modifier = modifier
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MonthList(
    monthList: List<Month>,
    onMonthClick: (Month) -> Unit,
    onSwipeDelete: (Month) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Состояния видимости для каждого элемента по id
    val visibleMap = remember { mutableStateMapOf<Int, Boolean>() }
    // Инициализация видимости для всех элементов (если ещё не инициализирована)
    LaunchedEffect(monthList) {
        monthList.forEach { month ->
            if (!visibleMap.containsKey(month.id)) {
                visibleMap[month.id] = true
            }
        }
    }

    Box(modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        ) {
            items(items = monthList, key = { it.id }) { item ->
                val visible = visibleMap[item.id] ?: true
                //9 Защита от повторного свайпа при настройках анимации.
                val alreadyDismissed = remember { mutableStateOf(false) }
                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        if (!alreadyDismissed.value &&
                            dismissValue == DismissValue.DismissedToStart ||
                            dismissValue == DismissValue.DismissedToEnd
                        ) {
                            alreadyDismissed.value = true

                            visibleMap[item.id] = false // запускаем анимацию исчезновения
                            false // НЕ удаляем сразу — ждём завершения анимации
                        } else {
                            false
                        }
                    }
                )
                // Анимация элемента списка
                AnimatedVisibility(
                    visible = visible,
                    //плавное появление элемента списка.
                    enter = expandVertically(animationSpec = tween(300)) + fadeIn(
                        animationSpec = tween(
                            300
                        )
                    ),
                    //плавное исчезновение элемента списка.
                    exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                ) {
                    // Когда элемент стал невидимым — вызываем удаление
                    LaunchedEffect(visible) {
                        if (!visible) {
                            delay(300) // Ждём окончания анимации

                            // Показываем Snackbar и ждём результата
                            val result = scope.launch {
                                val snackbarResult = snackbarHostState.showSnackbar(
                                    message = "УДАЛИТЬ?",
                                    actionLabel = "Отмена",
                                    duration = SnackbarDuration.Short
                                )
                                if (snackbarResult == SnackbarResult.ActionPerformed) {
                                    // Отмена удаления — восстанавливаем элемент
                                    visibleMap[item.id] = true
                                    alreadyDismissed.value = false
                                } else {
                                    // Подтверждаем удаление
                                    onSwipeDelete(item)
                                }
                            }
                        }
                    }

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(
                            DismissDirection.StartToEnd,
                            DismissDirection.EndToStart
                        ),
                        background = {
                            val direction = dismissState.dismissDirection
                            // Цвет фона для свайпа.
                            val color = when (direction) {
                                DismissDirection.StartToEnd -> Color.Red
                                DismissDirection.EndToStart -> Color.Red
                                null -> Color.Transparent
                            }
                            //Чтобы иконка удаления элемента была с обеих сторон во время свайпа.
                            val alignment = when (dismissState.dismissDirection) {
                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                DismissDirection.EndToStart -> Alignment.CenterEnd
                                null -> Alignment.CenterEnd
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    //отступ иконки от краёв экрана.
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Удалить",
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            MonthItem(
                                month = item,
                                calculated = monthCalculations(item),
                                modifier = Modifier
                                    .padding(dimensionResource(id = R.dimen.padding_small))
                                    .clickable { onMonthClick(item) }
                            )
                        }
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun MonthItem(
    month: Month,
    calculated: MonthCalculateData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        //назначение вцета для карточки, на основе выбранного цвета.
//        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.teal_700))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            //Строка с месяцем и годом.
            Text(
//                text = "2024, Апрель:",
                text = "${month.yearName}, ${month.monthName}",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            )
            //Разделитель
            HorizontalDivider(
//                color = colorResource(R.color.teal_200),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            //Строка с итоговой выплатой.
            Text(
                text = "${calculated.itog} руб.",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Preview
@Composable
fun MonthItemPreview() {
    val month = Month(
        id = 1,
        4,
        2025,
        2.0,
        3,
        4,
        5,
        6,
        7.0,
        8,
        9,
        9,
        15.0
    )
    val raschet = MonthCalculateData(
        rabTimeRub = 1000.0,
        nochTimeRub = 500.0,
        prikazNochRub = 200.0,
        premiaRub = 300.0,
        prazdTimeRub = 100.0,
        vislugaRub = 50.0,
        rayon20 = 400.0,
        severn30 = 600.0,
        rayon10 = 200.0,
        ndfl = 130.0,
        itog = 1500.0
    )
    MonthItem(
        month = month,
        MonthCalculateData(
            itog = raschet.itog,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun MonthListPreview() {
    val fakeMonths = listOf(
        Month(1,4,2025,10000.0,160,160,0,0,10.0,5,12,12,10000.0),
        Month(2,4,2025,10000.0,160,160,0,0,10.0,5,12,12,20000.0)
    )
    MaterialTheme {
        MonthList(
            fakeMonths,
            onMonthClick = {},
            onSwipeDelete = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MonthBodyPreview() {
    val fakeMonths = listOf(
        Month(1,4,2025,10000.0,160,160,0,0,10.0,5,12,12,10000.0),
        Month(2,4,2025,10000.0,160,160,0,0,10.0,5,12,12,20000.0)
    )
    MaterialTheme {
        HomeBody(
            fakeMonths,
            onMonthClick = {},
            onSwipeDelete = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    MaterialTheme {
        HomeBody(listOf(), onMonthClick = {}, onSwipeDelete = {})
    }
}