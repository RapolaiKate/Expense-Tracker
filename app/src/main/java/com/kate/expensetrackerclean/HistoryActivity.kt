package com.kate.expensetrackerclean

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.kate.expensetrackerclean.data.viewmodel.HistoryViewModel
import com.kate.expensetrackerclean.ui.history.HistoryTransaction
import com.kate.expensetrackerclean.ui.history.HistoryTransactionAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var transactionAdapter: HistoryTransactionAdapter

    private lateinit var rvTransactionHistory: RecyclerView

    private lateinit var tvHistoryYear: MaterialAutoCompleteTextView
    private lateinit var tvHistoryMonth: MaterialAutoCompleteTextView

    private lateinit var toggleHistoryType: MaterialButtonToggleGroup

    private lateinit var btnFilterAllTypes: MaterialButton
    private lateinit var btnFilterIncome: MaterialButton
    private lateinit var btnFilterExpense: MaterialButton

    private lateinit var tvFinancialSummary: TextView

    private var allTransactions: List<HistoryTransaction> = emptyList()

    private var totalIncome = 0.0
    private var totalExpenses = 0.0

    private val dateFormat =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        initializeViews()
        setupRecyclerView()
        setupFilters()
        observeTransactions()

        findViewById<View>(R.id.btnHistoryBack).setOnClickListener {
            finish()
        }
    }

    private fun initializeViews() {

        rvTransactionHistory =
            findViewById(R.id.rvTransactionHistory)

        tvHistoryYear =
            findViewById(R.id.tvHistoryYear)

        tvHistoryMonth =
            findViewById(R.id.tvHistoryMonth)

        toggleHistoryType =
            findViewById(R.id.toggleHistoryType)

        btnFilterAllTypes =
            findViewById(R.id.btnFilterAllTypes)

        btnFilterIncome =
            findViewById(R.id.btnFilterIncome)

        btnFilterExpense =
            findViewById(R.id.btnFilterExpense)

        tvFinancialSummary =
            findViewById(R.id.tvFinancialSummary)

        historyViewModel =
            ViewModelProvider(this)[HistoryViewModel::class.java]
    }

    private fun setupRecyclerView() {

        transactionAdapter = HistoryTransactionAdapter(
            emptyList(),

            onDetailsClick = { transaction ->
                openTransactionDetails(transaction)
            },

            onEditClick = { transaction ->
                openUpdateTransaction(transaction)
            },

            onDeleteClick = { transaction ->
                openDeleteTransaction(transaction)
            }
        )

        rvTransactionHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = transactionAdapter
        }
    }

    private fun setupFilters() {

        val currentYear = Calendar.getInstance()
            .get(Calendar.YEAR)

        val years = mutableListOf("All Years")

        for (year in currentYear downTo currentYear - 5) {
            years.add(year.toString())
        }

        val months = listOf(
            "All Months",
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )

        setupDropdown(tvHistoryYear, years)
        setupDropdown(tvHistoryMonth, months)

        tvHistoryYear.setText("All Years", false)
        tvHistoryMonth.setText("All Months", false)

        // Select All Types by default
        toggleHistoryType.check(R.id.btnFilterAllTypes)

        updateTypeButtonStyles()

        // Listen for Year and Month changes
        val filterListener =
            android.widget.AdapterView.OnItemClickListener { _, _, _, _ ->
                applyFilters()
            }

        tvHistoryYear.setOnItemClickListener(filterListener)
        tvHistoryMonth.setOnItemClickListener(filterListener)

        // Listen for transaction type button changes
        toggleHistoryType.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (isChecked) {
                updateTypeButtonStyles()
                applyFilters()
            }
        }
    }

    private fun updateTypeButtonStyles() {

        val selectedId = toggleHistoryType.checkedButtonId

        val selectedColor = Color.parseColor("#00C853")
        val unselectedColor = Color.parseColor("#15151D")

        val selectedTextColor = Color.BLACK
        val unselectedTextColor = Color.WHITE

        val buttons = listOf(
            btnFilterAllTypes,
            btnFilterIncome,
            btnFilterExpense
        )

        buttons.forEach { button ->

            val isSelected = button.id == selectedId

            button.backgroundTintList = ColorStateList.valueOf(
                if (isSelected) selectedColor else unselectedColor
            )

            button.setTextColor(
                if (isSelected) selectedTextColor else unselectedTextColor
            )
        }
    }

    private fun setupDropdown(
        dropdown: MaterialAutoCompleteTextView,
        items: List<String>
    ) {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            items
        )

        dropdown.setAdapter(adapter)
    }

    private fun observeTransactions() {

        historyViewModel.allTransactions.observe(this) { transactions ->

            allTransactions = transactions.sortedByDescending {
                it.date
            }

            applyFilters()
        }

        historyViewModel.totalIncome.observe(this) { income ->

            totalIncome = income ?: 0.0

            updateFinancialSummary()
        }

        historyViewModel.totalExpenses.observe(this) { expenses ->

            totalExpenses = expenses ?: 0.0

            updateFinancialSummary()
        }
    }

    private fun applyFilters() {

        if (!::transactionAdapter.isInitialized) return

        val selectedYear =
            tvHistoryYear.text.toString()

        val selectedMonth =
            tvHistoryMonth.text.toString()

        val selectedType = when (toggleHistoryType.checkedButtonId) {

            R.id.btnFilterIncome -> "Income"

            R.id.btnFilterExpense -> "Expense"

            else -> "All Types"
        }

        val calendar = Calendar.getInstance()

        val filteredTransactions = allTransactions.filter { transaction ->

            calendar.timeInMillis = transaction.date

            // Year filter
            val matchesYear =
                selectedYear == "All Years" ||
                        calendar.get(Calendar.YEAR).toString() == selectedYear

            // Month filter
            val selectedMonthNumber =
                if (selectedMonth == "All Months") {
                    0
                } else {
                    listOf(
                        "January", "February", "March",
                        "April", "May", "June",
                        "July", "August", "September",
                        "October", "November", "December"
                    ).indexOf(selectedMonth) + 1
                }

            val matchesMonth =
                selectedMonthNumber == 0 ||
                        calendar.get(Calendar.MONTH) + 1 == selectedMonthNumber

            // Transaction type filter
            val matchesType =
                selectedType == "All Types" ||
                        transaction.type == selectedType

            // All three filters must match
            matchesYear &&
                    matchesMonth &&
                    matchesType
        }

        // Display every matching transaction, newest first
        transactionAdapter.setTransactions(
            filteredTransactions.sortedByDescending { it.date }
        )

        updateFinancialSummary()
    }

    private fun updateFinancialSummary() {

        if (!::tvFinancialSummary.isInitialized) return

        tvFinancialSummary.text = String.format(
            Locale.getDefault(),
            "Total Income: R %,.2f\nTotal Expenses: R %,.2f\nBalance: R %,.2f",
            totalIncome,
            totalExpenses,
            totalIncome - totalExpenses
        )
    }

    private fun openTransactionDetails(
        transaction: HistoryTransaction
    ) {

        val intent = createTransactionIntent(
            TransactionDetailsActivity::class.java,
            transaction
        )

        startActivity(intent)
    }

    private fun openUpdateTransaction(
        transaction: HistoryTransaction
    ) {

        val intent = createTransactionIntent(
            UpdateTransactionActivity::class.java,
            transaction
        )

        startActivity(intent)
    }

    private fun openDeleteTransaction(
        transaction: HistoryTransaction
    ) {

        val intent = createTransactionIntent(
            DeleteTransactionActivity::class.java,
            transaction
        )

        startActivity(intent)
    }

    private fun createTransactionIntent(
        activityClass: Class<*>,
        transaction: HistoryTransaction
    ): Intent {

        return Intent(this, activityClass).apply {

            putExtra("TRANSACTION_ID", transaction.id)

            putExtra("TRANSACTION_TYPE", transaction.type)

            putExtra("TRANSACTION_TITLE", transaction.title)

            putExtra("TRANSACTION_AMOUNT", transaction.amount)

            putExtra("TRANSACTION_CATEGORY", transaction.category)

            putExtra(
                "TRANSACTION_DATE",
                dateFormat.format(Date(transaction.date))
            )

            putExtra("TRANSACTION_DATE_MILLIS", transaction.date)
        }
    }
}
