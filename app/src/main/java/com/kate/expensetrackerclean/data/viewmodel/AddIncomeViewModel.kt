package com.kate.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kate.expensetrackerclean.data.database.AppDatabase
import com.kate.expensetrackerclean.data.database.IncomeEntity
import com.kate.expensetrackerclean.data.repository.IncomeRepository
import kotlinx.coroutines.launch


class AddIncomeViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: IncomeRepository

    // ALL INCOME
    val allIncome: LiveData<List<IncomeEntity>>

    // TOTAL INCOME
    val totalAmount: LiveData<Double>

    // MONTHLY INCOME
    val monthlyAmount: LiveData<Double>

    init {

        // INITIALIZE DATABASE
        val incomeDao =
            AppDatabase.getDatabase(application).incomeDao()

        repository = IncomeRepository(incomeDao)

        allIncome = repository.allIncome
        totalAmount = repository.totalAmount
        monthlyAmount = repository.monthlyAmount
    }

    // INSERT INCOME
    fun insertIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.insertIncome(income)
        }
    }

    // UPDATE INCOME
    fun updateIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.updateIncome(income)
        }
    }

    // DELETE INCOME
    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }
}
