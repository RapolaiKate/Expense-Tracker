package com.kate.expensetrackerclean.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {

    // INSERT A NEW EXPENSE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    // UPDATE AN EXISTING EXPENSE
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expense_table WHERE id = CAST(:transactionId AS INTEGER)")
    suspend fun deleteExpenseById(transactionId: String): Int

    // DELETE AN EXPENSE
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // GET ALL EXPENSES, NEWEST FIRST
    @Query("SELECT * FROM expense_table ORDER BY id DESC")
    fun getAllExpenses(): LiveData<List<ExpenseEntity>>

    // GET TOTAL EXPENSES
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense_table")
    fun getTotalAmount(): LiveData<Double>

    // GET MONTHLY EXPENSES
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM expense_table
        WHERE strftime('%Y-%m', date / 1000, 'unixepoch')
        = strftime('%Y-%m', 'now')
    """)
    fun getMonthlyAmount(): LiveData<Double>
}
