package com.example.expensetrackerclean.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {

    // ===== INSERT A NEW EXPENSE =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // ===== UPDATE AN EXPENSE =====
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    // ===== DELETE AN EXPENSE =====
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // ===== GET ALL EXPENSES, NEWEST FIRST =====
    @Query("SELECT * FROM expense_table ORDER BY id DESC")
    fun getAllExpenses(): LiveData<List<ExpenseEntity>>

    // ===== GET TOTAL AMOUNT =====
    @Query("SELECT SUM(amount) FROM expense_table")
    fun getTotalAmount(): LiveData<Double>
    // ===== GET MONTHLY TOTAL AMOUNT =====
    @Query(""" SELECT SUM(amount) FROM expense_table WHERE strftime('%-Y-%m', date / 1000, 'unixepoch')
        = strftime('%Y-%m', 'now')""")
    fun getMonthlyAmount(): LiveData<Double>
}
