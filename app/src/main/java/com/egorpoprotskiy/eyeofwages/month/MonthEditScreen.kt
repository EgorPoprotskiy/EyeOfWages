package com.egorpoprotskiy.eyeofwages.month

import com.egorpoprotskiy.eyeofwages.R
import com.egorpoprotskiy.eyeofwages.navigation.NavigationDestination

object MonthEditDestination : NavigationDestination {
    override val route = "month_edit"
    override val titleRes = R.string.edit_raschet
    const val monthIdArg = "monthId"
    val routeWithArgs = "$route/{$monthIdArg}"
}

class MonthEditScreen {
}