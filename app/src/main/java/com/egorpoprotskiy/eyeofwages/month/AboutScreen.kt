package com.egorpoprotskiy.eyeofwages.month

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egorpoprotskiy.eyeofwages.MonthTopAppBar
import com.egorpoprotskiy.eyeofwages.R
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination

object AboutDestination: NavigationDestination {
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
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //Секция "Версия"
        item {
            InfoSection(
                title = stringResource(R.string.version_label),
                content = listOf(stringResource(R.string.version_number))
            )
        }
        //Секция "Новые функции"
        item {
            InfoSection(
                title = stringResource(R.string.features_added),
                content = listOf(
                    "• Реализован **автоматический расчёт отпускных** (Средний Дневной Заработок - СДЗ) на основе 12 предшествующих месяцев.",
                    "• Учёт исключаемых сумм (больничные) при расчёте СДЗ.",
                    "• Реализован Pull-to-Refresh на главном экране.",
                    "• Добавлена анимация исчезновения при удалении записи.",
                    "• Добавлен экран 'О приложении'."
                )
            )
        }
        //Секция "Исправленные ошибки"
        item {
            InfoSection(
                title = stringResource(R.string.bug_fixes),
                content = listOf(
                    "• **Исправлена проблема миграции БД**, обеспечивающая сохранность данных при обновлении.",
                    "• Исправлена ошибка сброса свайпа при отмене удаления.",
                    "• Улучшена обработка десятичных чисел."
                )
            )
        }
        //Секция "Планы на будущее"
        item {
            InfoSection(
                title = stringResource(R.string.future_plans),
                content = listOf(
                    "• Сохранение данных"
                )
            )
        }
    }
}

@Composable
private fun InfoSection(title: String, content: List<String>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content.forEach { item ->
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
    }
}
