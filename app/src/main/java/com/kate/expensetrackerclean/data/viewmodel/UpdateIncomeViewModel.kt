package com.kate.expensetrackerclean.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kate.expensetrackerclean.data.database.AppDatabase
import com.kate.expensetrackerclean.data.database.IncomeEntity
import com.kate.expensetrackerclean.data.repository.IncomeRepository
import kotlinx.coroutines.launch

class UpdateIncomeViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: IncomeRepository

    init {

        // GET DAO FROM DATABASE
        val incomeDao =
            AppDatabase.getDatabase(application).incomeDao()

        repository = IncomeRepository(incomeDao)
    }

    // UPDATE EXISTING INCOME
    fun updateIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.updateIncome(income)
        }
    }

    // DELETE EXISTING INCOME
    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }
}
