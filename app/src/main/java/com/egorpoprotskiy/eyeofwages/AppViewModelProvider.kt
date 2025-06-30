package com.egorpoprotskiy.eyeofwages

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.egorpoprotskiy.eyeofwages.home.HomeViewModel
import com.egorpoprotskiy.eyeofwages.month.MonthDetailsViewModel
import com.egorpoprotskiy.eyeofwages.month.MonthEditViewModel
import com.egorpoprotskiy.eyeofwages.month.MonthEntryViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            MonthEditViewModel(
                this.createSavedStateHandle(),
                monthApplication().container.monthRepository
            )
        }
        initializer {
            MonthEntryViewModel(
                monthApplication().container.monthRepository
            )
        }
        initializer {
            MonthDetailsViewModel(
                this.createSavedStateHandle(),
                monthApplication().container.monthRepository
            )
        }
        initializer {
            HomeViewModel(
                monthApplication().container.monthRepository)
        }
    }
}

fun CreationExtras.monthApplication(): MonthApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MonthApplication)