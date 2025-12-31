package com.example.expensetrackerclean.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.expensetrackerclean.data.database.ExpenseEntity
import com.example.expensetrackerclean.data.repository.ExpenseRepository

class StatsViewModel(private val repository: ExpenseRepository): ViewModel() {

    // ===== ALL EXPENSES (LiveData from repository) =====
    val allExpenses: LiveData<List<ExpenseEntity>> = repository.allExpenses

    // ===== TOTAL AMOUNT OF ALL EXPENSES =====
    val totalAmount: LiveData<Double> = repository.totalAmount
    // ===== MONTHLY AMOUNT OF EXPENSES =====
    val monthlyAmount: LiveData<Double> = repository.monthlyAmount
}

