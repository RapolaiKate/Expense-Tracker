
package com.kate.expensetrackerclean.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.database.IncomeEntity
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import com.kate.expensetrackerclean.data.repository.IncomeRepository

class StatsViewModel(
    expenseRepository: ExpenseRepository,
    incomeRepository: IncomeRepository
) : ViewModel() {

    // EXPENSE STATISTICS
    val allExpenses: LiveData<List<ExpenseEntity>> =
        expenseRepository.allExpenses

    val totalExpenses: LiveData<Double> =
        expenseRepository.totalAmount

    val monthlyExpenses: LiveData<Double> =
        expenseRepository.monthlyAmount

    // INCOME STATISTICS
    val allIncome: LiveData<List<IncomeEntity>> =
        incomeRepository.allIncome

    val totalIncome: LiveData<Double> =
        incomeRepository.totalAmount

    val monthlyIncome: LiveData<Double> =
        incomeRepository.monthlyAmount
}

