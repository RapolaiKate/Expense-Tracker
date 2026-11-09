package com.kate.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kate.expensetrackerclean.data.database.AppDatabase
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class AddExpenseViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: ExpenseRepository

    // ALL EXPENSES
    val allExpenses: LiveData<List<ExpenseEntity>>

    // TOTAL EXPENSES
    val totalAmount: LiveData<Double>

    // MONTHLY EXPENSES
    val monthlyAmount: LiveData<Double>

    init {

        // INITIALIZE DATABASE
        val expenseDao =
            AppDatabase.getDatabase(application).expenseDao()

        repository = ExpenseRepository(expenseDao)

        allExpenses = repository.allExpenses
        totalAmount = repository.totalAmount
        monthlyAmount = repository.monthlyAmount
    }

    // INSERT EXPENSE
    fun insertExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }

    // UPDATE EXPENSE
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    // DELETE EXPENSE
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}
