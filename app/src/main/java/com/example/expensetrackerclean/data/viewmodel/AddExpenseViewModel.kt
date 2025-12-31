package com.example.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerclean.data.database.ExpenseDatabase
import com.example.expensetrackerclean.data.database.ExpenseEntity
import com.example.expensetrackerclean.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class AddExpenseViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ExpenseRepository


    // ===== LIVEDATA LIST AND TOTAL AMOUNT OF ALL EXPENSES FOR DASHBOARD DISPLAY =====
    val allExpenses: LiveData<List<ExpenseEntity>>
    val totalAmount: LiveData<Double>

    init {
        // ===== INITIALIZE DATABASE =====
        val expenseDao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)

        allExpenses = repository.allExpenses
        totalAmount = repository.totalAmount
    }
    // ===== INSERT EXPENSE =====
    fun insertExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }
    // ===== UPDATE EXPENSE =====
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }
    // ===== DELETE EXPENSE =====
    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}


