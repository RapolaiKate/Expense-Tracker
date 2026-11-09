package com.kate.expensetrackerclean

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    private lateinit var cardIncome: MaterialCardView
    private lateinit var cardExpense: MaterialCardView
    private lateinit var cardHistory: MaterialCardView
    private lateinit var cardDashboard: MaterialCardView
    private lateinit var cardIncomeStats: MaterialCardView
    private lateinit var cardExpenseStats: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Initialize all six cards
        cardIncome = findViewById(R.id.cardIncome)
        cardExpense = findViewById(R.id.cardExpense)
        cardHistory = findViewById(R.id.cardHistory)
        cardDashboard = findViewById(R.id.cardDashboard)
        cardIncomeStats = findViewById(R.id.cardIncomeStats)
        cardExpenseStats = findViewById(R.id.cardExpenseStats)

        // Open Income Screen
        cardIncome.setOnClickListener {
            startActivity(Intent(this, IncomeActivity::class.java))
        }

        // Open Expense Screen
        cardExpense.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }

        // Open History Screen
        cardHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Open Dashboard Screen
        cardDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardHostActivity::class.java))
        }

        // Open Income Statistics
        cardIncomeStats.setOnClickListener {
            openStatistics("INCOME")
        }

        // Open Expense Statistics
        cardExpenseStats.setOnClickListener {
            openStatistics("EXPENSE")
        }
    }

    // Open Statistics Screen
    private fun openStatistics(type: String) {
        val intent = Intent(this, StatsActivity::class.java)
        intent.putExtra(StatsActivity.EXTRA_TRANSACTION_TYPE, type)
        startActivity(intent)
    }
}
