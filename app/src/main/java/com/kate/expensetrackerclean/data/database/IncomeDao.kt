package com.kate.expensetrackerclean.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface IncomeDao {

    // INSERT A NEW INCOME
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    // UPDATE EXISTING INCOME
    @Update
    suspend fun updateIncome(income: IncomeEntity)

    // DELETE INCOME
    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    // DELETE INCOME BY ID
    @Query("DELETE FROM income_table WHERE id = CAST(:transactionId AS INTEGER)")
    suspend fun deleteIncomeById(transactionId: String): Int

    // GET ALL INCOME, NEWEST FIRST
    @Query("SELECT * FROM income_table ORDER BY id DESC")
    fun getAllIncome(): LiveData<List<IncomeEntity>>

    // GET TOTAL INCOME
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM income_table")
    fun getTotalIncome(): LiveData<Double>

    // GET MONTHLY INCOME
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0)
        FROM income_table
        WHERE strftime('%Y-%m', date / 1000, 'unixepoch')
        = strftime('%Y-%m', 'now')
    """)
    fun getMonthlyIncome(): LiveData<Double>
}
