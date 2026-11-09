package com.kate.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kate.expensetrackerclean.data.database.AppDatabase
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class UpdateExpenseViewModel(application: Application) :
    AndroidViewModel(application) {

    // REPOSITORY INSTANCE
    private val repository: ExpenseRepository

    init {

        // GET DAO FROM DATABASE
        val expenseDao =
            AppDatabase.getDatabase(application).expenseDao()

        repository = ExpenseRepository(expenseDao)
    }

    // UPDATE EXISTING EXPENSE
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    // DELETE EXISTING EXPENSE
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}
