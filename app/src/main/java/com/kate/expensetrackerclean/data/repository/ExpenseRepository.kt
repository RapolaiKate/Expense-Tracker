package com.kate.expensetrackerclean.data.repository

import androidx.lifecycle.LiveData
import com.kate.expensetrackerclean.data.database.ExpenseDao
import com.kate.expensetrackerclean.data.database.ExpenseEntity

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // GET ALL EXPENSES
    val allExpenses: LiveData<List<ExpenseEntity>> =
        expenseDao.getAllExpenses()

    // GET TOTAL EXPENSES
    val totalAmount: LiveData<Double> =
        expenseDao.getTotalAmount()

    // GET MONTHLY EXPENSES
    val monthlyAmount: LiveData<Double> =
        expenseDao.getMonthlyAmount()

    // INSERT AN EXPENSE
    suspend fun insertExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    // UPDATE AN EXPENSE
    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    // DELETE AN EXPENSE
    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    // DELETE EXPENSE BY ID
    suspend fun deleteExpenseById(transactionId: String): Int {
        return expenseDao.deleteExpenseById(transactionId)
    }
}

