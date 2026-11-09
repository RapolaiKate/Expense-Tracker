
package com.kate.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.kate.expensetrackerclean.data.database.AppDatabase
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.database.IncomeEntity
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import com.kate.expensetrackerclean.data.repository.IncomeRepository
import com.kate.expensetrackerclean.ui.history.HistoryTransaction
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) :
    AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val expenseRepository =
        ExpenseRepository(database.expenseDao())

    private val incomeRepository =
        IncomeRepository(database.incomeDao())

    // COMBINED TRANSACTION HISTORY
    private val _allTransactions =
        MediatorLiveData<List<HistoryTransaction>>()

    val allTransactions: LiveData<List<HistoryTransaction>> =
        _allTransactions

    private var expenses: List<ExpenseEntity> = emptyList()
    private var incomes: List<IncomeEntity> = emptyList()

    // TOTAL INCOME
    val totalIncome: LiveData<Double> =
        incomeRepository.totalAmount

    // TOTAL EXPENSES
    val totalExpenses: LiveData<Double> =
        expenseRepository.totalAmount

    init {
        _allTransactions.addSource(expenseRepository.allExpenses) { expenseList ->
            expenses = expenseList
            combineTransactions()
        }

        _allTransactions.addSource(incomeRepository.allIncome) { incomeList ->
            incomes = incomeList
            combineTransactions()
        }
    }

    // COMBINE INCOME AND EXPENSES
    private fun combineTransactions() {

        val expenseTransactions = expenses.map { expense ->
            HistoryTransaction(
                id = expense.id,
                title = expense.title,
                amount = expense.amount,
                category = expense.category,
                date = expense.date,
                type = "Expense"
            )
        }

        val incomeTransactions = incomes.map { income ->
            HistoryTransaction(
                id = income.id,
                title = income.title,
                amount = income.amount,
                category = income.category,
                date = income.date,
                type = "Income"
            )
        }

        _allTransactions.value =
            (incomeTransactions + expenseTransactions)
                .sortedByDescending { it.date }
    }

    // DELETE TRANSACTION
    fun deleteTransaction(transaction: HistoryTransaction) {
        viewModelScope.launch {
            when (transaction.type) {
                "Income" -> {
                    val income = incomes.find {
                        it.id == transaction.id
                    }
                    income?.let {
                        incomeRepository.deleteIncome(it)
                    }
                }

                "Expense" -> {
                    val expense = expenses.find {
                        it.id == transaction.id
                    }
                    expense?.let {
                        expenseRepository.deleteExpense(it)
                    }
                }
            }
        }
    }

    // DELETE TRANSACTION BY ID AND TYPE
    suspend fun deleteTransactionById(
        transactionId: String,
        transactionType: String
    ): Int {

        return when (transactionType) {
            "Income" -> incomeRepository.deleteIncomeById(transactionId)
            "Expense" -> expenseRepository.deleteExpenseById(transactionId)
            else -> 0
        }
    }

    // UPDATE INCOME
    fun updateIncome(income: IncomeEntity) {
        viewModelScope.launch {
            incomeRepository.updateIncome(income)
        }
    }

    // UPDATE EXPENSE
    fun updateExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.updateExpense(expense)
        }
    }
}
