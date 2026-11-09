package com.kate.expensetrackerclean.data.repository

import androidx.lifecycle.LiveData
import com.kate.expensetrackerclean.data.database.IncomeDao
import com.kate.expensetrackerclean.data.database.IncomeEntity

class IncomeRepository(private val incomeDao: IncomeDao) {

    // GET ALL INCOME
    val allIncome: LiveData<List<IncomeEntity>> =
        incomeDao.getAllIncome()

    // GET TOTAL INCOME
    val totalAmount: LiveData<Double> =
        incomeDao.getTotalIncome()

    // GET MONTHLY INCOME
    val monthlyAmount: LiveData<Double> =
        incomeDao.getMonthlyIncome()

    // INSERT INCOME
    suspend fun insertIncome(income: IncomeEntity) {
        incomeDao.insertIncome(income)
    }

    // UPDATE INCOME
    suspend fun updateIncome(income: IncomeEntity) {
        incomeDao.updateIncome(income)
    }

    // DELETE INCOME
    suspend fun deleteIncome(income: IncomeEntity) {
        incomeDao.deleteIncome(income)

    }

    // DELETE INCOME BY ID
        suspend fun deleteIncomeById(transactionId: String): Int {
            return incomeDao.deleteIncomeById(transactionId)
        }
    }

