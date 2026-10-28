package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import com.egorpoprotskiy.eyeofwages.R
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination

object AboutDestination : NavigationDestination {
    override val route = "about"
    override val titleRes = R.string.about_app
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MonthTopAppBar(
                title = stringResource(AboutDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        AboutBody(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun AboutBody(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "EYE OF WAGES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Расчёт зарплаты — без лишней сложности",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "История расчётов, статистика, графики и отпускные в одном месте.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                    )
                }
            }
        }

        item {
            InfoSection(
                title = stringResource(R.string.version_label),
                content = listOf("1.5.0")
            )
        }

        item {
            InfoSection(
                title = stringResource(R.string.features_added),
                content = listOf(
                    "Обновлённый главный экран с обзором зарплаты и истории расчётов.",
                    "График изменения зарплаты за выбранный период.",
                    "Статистика и сравнение с предыдущим месяцем.",
                    "Поиск и сортировка записей в истории расчётов.",
                    "Улучшено отображение информации о начислениях и отпускных."
                )
            )
        }

        item {
            InfoSection(
                title = stringResource(R.string.prev_features_added),
                content = listOf(
                    "Расчёт отпускных на основе среднего дневного заработка (СДЗ) за 12 предшествующих месяцев.",
                    "Учёт исключаемых сумм при расчёте среднего дневного заработка.",
                    "Дополнительные выплаты: премии и другие начисления.",
                    "Обновление списка жестом потягивания вниз.",
                    "Анимация при удалении записи.",
                    "Отдельный экран с информацией о приложении."
                )
            )
        }

        item {
            InfoSection(
                title = stringResource(R.string.bug_fixes),
                content = listOf(
                    "Исправления и улучшения стабильности приложения.",
                    "Улучшена обработка десятичных чисел.",
                    "Улучшено взаимодействие с историей расчётов."
                )
            )
        }

        item {
            InfoSection(
                title = stringResource(R.string.future_plans),
                content = listOf(
                    "Дальнейшее развитие расчётов зарплаты.",
                    "Улучшение аналитики и удобства работы с историей."
                )
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, content: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 17.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            content.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutBody()
}