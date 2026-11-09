
package com.kate.expensetrackerclean.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import com.kate.expensetrackerclean.data.repository.IncomeRepository

class StatsViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val incomeRepository: IncomeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(
                expenseRepository,
                incomeRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
