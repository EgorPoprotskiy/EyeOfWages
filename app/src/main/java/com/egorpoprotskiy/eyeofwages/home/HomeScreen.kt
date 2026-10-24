package com.egorpoprotskiy.eyeofwages.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalContext
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
import com.egorpoprotskiy.eyeofwages.data.MonthCalculations
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.set
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object HomeDestination: NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navigateToMonthEntry: () -> Unit,
    navigateToMonthUpdate: (Int) -> Unit,
    navigateToAbout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val homeUiSate by viewModel.homeUiState.collectAsState()
    // Получаем состояние загрузки из ViewModel для PULL-TO-REFRESH
    val isRefreshingState: State<Boolean> = viewModel.isRefreshing.collectAsState()
    // Новое состояние для Pull-to-Refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshingState.value,
        onRefresh = viewModel::refreshData
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MonthTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                showAction = true,
                onActionClick = navigateToAbout
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToMonthEntry,
                shape = RoundedCornerShape(18.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_large))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.vvod_dannyh)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState)
        )
        {
            HomeBody(
                monthList = homeUiSate.monthList,
                onMonthClick = navigateToMonthUpdate,
                onSwipeDelete = { month ->
                    coroutineScope.launch {
                        viewModel.deleteMonth(month)
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
            PullRefreshIndicator(
                refreshing = isRefreshingState.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun HomeBody(
    monthList: List<Month>,
    onMonthClick: (Int) -> Unit,
    onSwipeDelete: (Month) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Заголовок, график, статистика и список используют один LazyColumn,
    // поэтому список остаётся доступен даже на небольших экранах.
    MonthList(
        monthList = monthList,
        onMonthClick = { onMonthClick(it.id) },
        onSwipeDelete = onSwipeDelete,
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Сводная карточка показывает последний сохранённый расчёт.
                SalaryOverviewCard(monthList = monthList)

                if (monthList.size >= 2) {
                    SalaryHistoryChart(monthList = monthList)
                }

                if (monthList.isNotEmpty()) {
                    SalaryStatsCard(monthList = monthList)
                } else {
                    Text(
                        text = "Добавь первый месяц, чтобы здесь появилась история зарплаты.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                    )
                }
            }
        }
    )
}

@Composable
private fun SalaryOverviewCard(monthList: List<Month>) {
    val latestMonth = monthList.maxWithOrNull(
        compareBy<Month> { it.yearName }.thenBy { it.monthName }
    )
    val latestSalary = latestMonth?.let { MonthCalculations(it).itog }

    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "ПОСЛЕДНИЙ РАСЧЁТ",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = latestSalary?.let(::formatRubles) ?: "Пока нет данных",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = latestMonth?.let { "${it.monthName}.${it.yearName}" } ?: "Добавь первый месяц",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
                Text(
                    text = "Месяцев: ${monthList.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SalaryStatsCard(monthList: List<Month>) {
    // Статистика рассчитывается из уже сохранённых месяцев и не меняет данные в базе.
    val sortedMonths = monthList.sortedWith(
        compareBy<Month> { it.yearName }.thenBy { it.monthName }
    )
    val salaries = sortedMonths.map { MonthCalculations(it).itog }
    val averageSalary = salaries.average()
    val minSalary = salaries.minOrNull() ?: 0.0
    val maxSalary = salaries.maxOrNull() ?: 0.0
    val latestSalary = salaries.lastOrNull()
    val previousSalary = salaries.getOrNull(salaries.lastIndex - 1)
    val difference = if (latestSalary != null && previousSalary != null) {
        latestSalary - previousSalary
    } else null
    val percentChange = if (difference != null && previousSalary != null && previousSalary != 0.0) {
        difference / kotlin.math.abs(previousSalary) * 100.0
    } else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "СТАТИСТИКА ЗАРПЛАТЫ",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SalaryStatItem(
                    title = "Средняя",
                    value = formatRubles(averageSalary),
                    modifier = Modifier.weight(1f)
                )
                SalaryStatItem(
                    title = "Максимум",
                    value = formatRubles(maxSalary),
                    modifier = Modifier.weight(1f)
                )
                SalaryStatItem(
                    title = "Минимум",
                    value = formatRubles(minSalary),
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Изменение к предыдущему месяцу",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (difference != null) {
                    val sign = if (difference > 0.0) "+" else if (difference < 0.0) "−" else ""
                    val percentText = percentChange?.let {
                        " (${if (it > 0) "+" else ""}${String.format(Locale("ru", "RU"), "%.1f", it)}%)"
                    } ?: ""
                    val changeColor = when {
                        difference > 0.0 -> Color(0xFF16845B)
                        difference < 0.0 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    val changeSymbol = when {
                        difference > 0.0 -> "↑"
                        difference < 0.0 -> "↓"
                        else -> "→"
                    }
                    Row(
                        modifier = Modifier
                            .background(
                                color = changeColor.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = changeSymbol,
                            style = MaterialTheme.typography.titleLarge,
                            color = changeColor,
                            fontWeight = FontWeight.Bold
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "$sign${formatRubles(kotlin.math.abs(difference))}$percentText",
                                style = MaterialTheme.typography.titleMedium,
                                color = changeColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = when {
                                    difference > 0.0 -> "Зарплата выросла"
                                    difference < 0.0 -> "Зарплата снизилась"
                                    else -> "Без изменений"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Добавь ещё один месяц для сравнения",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SalaryStatItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2
        )
    }
}

@Composable
private fun SalaryHistoryChart(monthList: List<Month>) {
    // Период хранится только в состоянии интерфейса и не меняет сохранённые данные.
    var selectedPeriod by rememberSaveable { mutableStateOf("6") }
    var selectedPointIndex by rememberSaveable { mutableStateOf(0) }
    val sortedMonths = monthList.sortedWith(
        compareBy<Month> { it.yearName }.thenBy { it.monthName }
    )
    val selectedMonths = when (selectedPeriod) {
        "3" -> sortedMonths.takeLast(3)
        "6" -> sortedMonths.takeLast(6)
        "12" -> sortedMonths.takeLast(12)
        else -> sortedMonths
    }
    val history = selectedMonths.map { month ->
        month.monthName to MonthCalculations(month).itog
    }
    // Показываем ограниченное число подписей, чтобы они не накладывались друг на друга.
    val labelStep = ((selectedMonths.size + 5) / 6).coerceAtLeast(1)
    val monthLabels = selectedMonths.mapIndexed { index, month ->
        val isFirst = index == 0
        val isLast = index == selectedMonths.lastIndex
        val isYearChange = index > 0 &&
            selectedMonths[index - 1].yearName != month.yearName
        if (isFirst || isLast || index % labelStep == 0 || isYearChange) {
            // monthName хранится в базе как число от 1 до 12, поэтому преобразуем его в короткое название.
            val shortMonth = listOf(
                "Янв", "Фев", "Мар", "Апр", "Май", "Июн",
                "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"
            ).getOrNull(month.monthName - 1) ?: month.monthName.toString()
            if (isFirst || isYearChange || isLast) "$shortMonth ${month.yearName}" else shortMonth
        } else {
            ""
        }
    }

    if (history.size < 2) return

    val values = history.map { it.second }
    val activeIndex = selectedPointIndex.coerceIn(0, history.lastIndex)
    val activeMonth = history[activeIndex]
    val minValue = minOf(0.0, values.minOrNull() ?: 0.0)
    val maxValue = values.maxOrNull() ?: 0.0
    val range = (maxValue - minValue).takeIf { it > 0.0 } ?: 1.0

    // Читаем цвета темы в composable-контексте, до входа в Canvas DrawScope.
    val chartColor = MaterialTheme.colorScheme.primary
    val baselineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
    val pointFillColor = MaterialTheme.colorScheme.surfaceContainerLow
    val chipSelectedColor = MaterialTheme.colorScheme.primaryContainer
    val chipUnselectedColor = MaterialTheme.colorScheme.surfaceVariant
    val chipSelectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
    val chipUnselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ДИНАМИКА ЗАРПЛАТЫ",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("3", "6", "12", "Всё").forEach { period ->
                    val selected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selected) chipSelectedColor else chipUnselectedColor,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable {
                        selectedPeriod = period
                        selectedPointIndex = 0
                    }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (period == "Всё") period else "$period мес.",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) chipSelectedTextColor else chipUnselectedTextColor,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Text(
                text = "Показано месяцев: ${history.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .height(112.dp)
                    .pointerInput(history) {
                        detectTapGestures { tap ->
                            // Выбираем точку, ближайшую к месту касания по горизонтали.
                            val horizontalPadding = 8.dp.toPx()
                            val chartWidth = size.width - horizontalPadding * 2
                            val relativeX = (tap.x - horizontalPadding).coerceIn(0f, chartWidth)
                            val nearestIndex = (relativeX / chartWidth * (history.size - 1))
                                .toInt()
                                .coerceIn(0, history.lastIndex)
                            selectedPointIndex = nearestIndex
                        }
                    }
            ) {
                val horizontalPadding = 8.dp.toPx()
                val verticalPadding = 10.dp.toPx()
                val chartWidth = size.width - horizontalPadding * 2
                val chartHeight = size.height - verticalPadding * 2
                val points = values.mapIndexed { index, value ->
                    val x = horizontalPadding + chartWidth * index / (values.size - 1)
                    val y = verticalPadding + chartHeight * (1f - ((value - minValue) / range).toFloat())
                    Offset(x, y)
                }

                drawLine(
                    color = baselineColor,
                    start = Offset(horizontalPadding, size.height - verticalPadding),
                    end = Offset(size.width - horizontalPadding, size.height - verticalPadding),
                    strokeWidth = 1.dp.toPx()
                )
                val path = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = path,
                    color = chartColor,
                    style = Stroke(width = 3.dp.toPx())
                )
                points.forEachIndexed { index, point ->
                    // У выбранного месяца точка крупнее, чтобы выделить его на графике.
                    drawCircle(
                        color = chartColor,
                        radius = if (index == activeIndex) 6.dp.toPx() else 4.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = pointFillColor,
                        radius = if (index == activeIndex) 2.8.dp.toPx() else 1.8.dp.toPx(),
                        center = point
                    )
                }
            }
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                monthLabels.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = activeMonth.first.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatRubles(activeMonth.second),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Максимум за период: ${formatRubles(maxValue)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatRubles(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    formatter.currency = Currency.getInstance("RUB")
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 2
    return formatter.format(value)
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MonthList(
    monthList: List<Month>,
    onMonthClick: (Month) -> Unit,
    onSwipeDelete: (Month) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Локальный поиск по названию месяца и году не меняет сохранённые данные.
    var monthSearchQuery by rememberSaveable { mutableStateOf("") }
    // Пользователь может менять порядок истории, не меняя сохранённые записи.
    var newestFirst by rememberSaveable { mutableStateOf(true) }
    val monthNames = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    val filteredMonthList = remember(monthList, monthSearchQuery, newestFirst) {
        val query = monthSearchQuery.trim().lowercase(Locale("ru", "RU"))
        val matchingMonths = if (query.isBlank()) monthList else monthList.filter { month ->
            val monthLabel = monthNames.getOrNull(month.monthName - 1)
                ?: month.monthName.toString()
            monthLabel.lowercase(Locale("ru", "RU")).contains(query) ||
                month.yearName.toString().contains(query)
        }
        val chronological = matchingMonths.sortedWith(
            compareBy<Month> { it.yearName }.thenBy { it.monthName }
        )
        if (newestFirst) chronological.reversed() else chronological
    }

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
        ) {
            item(key = "home_header") {
                header()
            }
            item(key = "month_search") {
                OutlinedTextField(
                    value = monthSearchQuery,
                    onValueChange = { monthSearchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    singleLine = true,
                    label = { Text("Поиск по месяцу или году") },
                    placeholder = { Text("Например, Октябрь или 2026") },
                    shape = RoundedCornerShape(16.dp)
                )
            }
            item(key = "month_sort_controls") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { newestFirst = true }, enabled = !newestFirst) {
                        Text("Сначала новые")
                    }
                    TextButton(onClick = { newestFirst = false }, enabled = newestFirst) {
                        Text("Сначала старые")
                    }
                }
            }
            if (filteredMonthList.isEmpty() && monthList.isNotEmpty()) {
                item(key = "month_search_empty") {
                    Text(
                        text = "Ничего не найдено. Попробуй изменить запрос.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                    )
                }
            }
            items(items = filteredMonthList, key = { it.id }) { item ->
                val visible = visibleMap[item.id] ?: true
                //9 Защита от повторного свайпа при настройках анимации.
//                val alreadyDismissed = remember { mutableStateOf(false) }

                // 42 Переменная для состояния диалога подтверждения удаления(по-умолчанию false).
                var showConfirmDialog by rememberSaveable { mutableStateOf(false) }

                val dismissState = rememberDismissState(
                    confirmStateChange = { dismissValue ->
                        if (
//                            !alreadyDismissed.value &&
                            dismissValue == DismissValue.DismissedToStart ||
                            dismissValue == DismissValue.DismissedToEnd
                        ) {
//                            alreadyDismissed.value = true
//                            visibleMap[item.id] = false // запускаем анимацию исчезновения
                            showConfirmDialog = true // Запрос подтверждения удаления
                            false // НЕ удаляем сразу — ждём завершения анимации
                        } else {
                            false
                        }
                    }
                )
                //42 Диалоговое окно подтверждения удаления
                if (showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            // Пользователь нажал вне диалога или назад, отменяем удаление
                            showConfirmDialog = false
                            scope.launch {
                                dismissState.reset() // Сбрасываем состояние свайпа
                            }
                        },
                        title = { Text(text = stringResource(R.string.attention)) },
                        text = { Text(text = stringResource(R.string.delete_question)) },
                        confirmButton = {
                            TextButton(onClick = {
                                showConfirmDialog = false
                                visibleMap[item.id] = false // Запускаем анимацию исчезновения
                                // onSwipeDelete(item) // Переместим вызов удаления в LaunchedEffect, чтобы дождаться анимации
                            }) {
                                Text(stringResource(R.string.yes))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showConfirmDialog = false
                                scope.launch {
                                    dismissState.reset() // Сбрасываем состояние свайпа
                                }
                            }) {
                                Text(stringResource(R.string.no))
                            }
                        }
                    )
                }

                val context = LocalContext.current
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
                            delay(150) // Ждём окончания анимации
                            // Для дебага(показывает на экране ID элемента, который был удален)
//                            Toast.makeText(context, "Удаляю ID: ${item.id}", Toast.LENGTH_SHORT).show()
                            //42
                            onSwipeDelete(item)
                        }
                    }
                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(
                            DismissDirection.StartToEnd,
                            DismissDirection.EndToStart
                        ),
                        background = {
/*
                            // Цвет фона для свайпа.
                            val direction = dismissState.dismissDirection
                            val color = when (direction) {
                                DismissDirection.StartToEnd -> Color.Red
                                DismissDirection.EndToStart -> Color.Red
                                null -> Color.Transparent
                            }
 */
                            //Чтобы иконка удаления элемента была с обеих сторон во время свайпа.
                            val alignment = when (dismissState.dismissDirection) {
                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                DismissDirection.EndToStart -> Alignment.CenterEnd
                                null -> Alignment.CenterEnd
                            }
                            // Красный фон и корзина появляются только во время активного свайпа.
                            // В обычном состоянии фон полностью прозрачный, чтобы не окрашивать список.
                            val isSwiping = dismissState.dismissDirection != null
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (isSwiping) MaterialTheme.colorScheme.errorContainer
                                        else Color.Transparent
                                    )
                                    // Отступ иконки от краёв экрана.
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                if (isSwiping) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Удалить",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        },
                        dismissContent = {
                            MonthItem(
                                month = item,
                                calculated = MonthCalculations(item),
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
    // Преобразуем номер месяца из модели в короткое русское название для списка.
    val monthLabel = listOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    ).getOrNull(month.monthName - 1) ?: month.monthName.toString()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Верхняя строка помогает быстро найти нужный месяц и год.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = month.yearName.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Итоговая выплата визуально отделена от подписи месяца.
            Text(
                text = formatRubles(calculated.itog),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
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
        15.0,
        777.0,
        bolnichniy = 777.0,
        otpuskDays = 7,
        otpuskPay = 777.0,
        otherPayments = 50000.0
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
        Month(1,4,2025,10000.0,160,160,0,0,10.0,5,12,12,10000.0, 777.0, 777.0, 777, 777.0, 50000.0),
        Month(2,4,2025,10000.0,160,160,0,0,10.0,5,12,12,20000.0, 777.0, 777.0, 777, 777.0, 50000.0)
    )
    com.egorpoprotskiy.eyeofwages.ui.theme.EyeOfWagesTheme {
        MonthList(
            fakeMonths,
            onMonthClick = {},
            onSwipeDelete = {},
            header = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MonthBodyPreview() {
    val fakeMonths = listOf(
        Month(1,4,2025,10000.0,160,160,0,0,10.0,5,12,12,10000.0, 777.0, 777.0, 777, 777.0, 50000.0),
        Month(2,4,2025,10000.0,160,160,0,0,10.0,5,12,12,20000.0, 777.0, 777.0, 777, 777.0, 50000.0)
    )
    com.egorpoprotskiy.eyeofwages.ui.theme.EyeOfWagesTheme {
        HomeBody(
            fakeMonths,
            onMonthClick = {},
            onSwipeDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    com.egorpoprotskiy.eyeofwages.ui.theme.EyeOfWagesTheme {
        HomeBody(listOf(), onMonthClick = {}, onSwipeDelete = {})
    }
}
