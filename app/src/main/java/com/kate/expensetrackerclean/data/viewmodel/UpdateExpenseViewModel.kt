package com.kate.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kate.expensetrackerclean.data.database.ExpenseDatabase
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class UpdateExpenseViewModel(application: Application): AndroidViewModel(application) {

    // ===== REPO INSTANCE =====
    private val repository: ExpenseRepository

    init {
        // ===== GET DAO FROM DATABASE =====
        val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)
    }
    // ===== UPDATE & DELETE EXISTING EXPENSE =====
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}