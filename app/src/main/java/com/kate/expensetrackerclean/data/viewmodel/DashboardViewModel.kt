package com.kate.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import com.kate.expensetrackerclean.data.database.AppDatabase


class DashboardViewModel(application: Application): AndroidViewModel(application) {

    private val repository: ExpenseRepository

    // ===== LIVEDATA LIST AND TOTAL AMOUNT OF ALL EXPENSES FOR DASHBOARD DISPLAY =====
    val allExpenses: LiveData<List<ExpenseEntity>>
    val totalAmount: LiveData<Double>

    init {
        // ===== INITIALIZE DAO & REPO =====
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)

        // ===== READ DATA FROM REPOSITORY =====
        allExpenses = repository.allExpenses
        totalAmount = repository.totalAmount
    }

    fun insertExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.insertExpense(expense)
        }
    }
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


