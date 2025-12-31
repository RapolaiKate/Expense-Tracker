package com.example.expensetrackerclean.data.repository

import androidx.lifecycle.LiveData
import com.example.expensetrackerclean.data.database.ExpenseDao
import com.example.expensetrackerclean.data.database.ExpenseEntity


class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // ===== LIVEDATA FOR ALL EXPENSES FROM THE DATABASE =====
    val allExpenses: LiveData<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    // ===== LIVEDATA FOR THE TOTAL AMOUNT OF ALL EXPENSES =====
    val totalAmount: LiveData<Double>  = expenseDao.getTotalAmount()

    // ===== LIVEDATA FOR MONTHLY AMOUNT OF EXPENSES =====
    val monthlyAmount: LiveData<Double> = expenseDao.getMonthlyAmount()

    // ===== LIVEDATA FOR THE TOTAL MONTHLY AMOUNT EXPENSE =====

    // ===== INSERT A NEW EXPENSE =====
    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }
    // ===== UPDATE AN EXISTING EXPENSE =====
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }
    // ===== DELETE AN EXPENSE =====
    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }
}
