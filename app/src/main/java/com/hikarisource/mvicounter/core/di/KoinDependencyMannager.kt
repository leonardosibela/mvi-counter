package com.hikarisource.mvicounter.core.di

import com.hikarisource.mvicounter.presentation.viewmodel.CounterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val viewModelModules = module {
    viewModel { CounterViewModel() }
}

object KoinDependencyManager {
    val modules = module { }.apply {
        includes(viewModelModules)
    }
}