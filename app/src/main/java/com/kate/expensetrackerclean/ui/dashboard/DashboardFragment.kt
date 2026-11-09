package com.kate.expensetrackerclean.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kate.expensetrackerclean.R
import com.kate.expensetrackerclean.TransactionDetailsActivity
import com.kate.expensetrackerclean.data.viewmodel.HistoryViewModel
import com.kate.expensetrackerclean.ui.history.HistoryTransaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class DashboardFragment : Fragment(R.layout.activity_dashboard) {

    private lateinit var btnBack: ImageView
    private lateinit var spinnerMonth: Spinner
    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var tvTotalBalance: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpenses: TextView

    private lateinit var recyclerViewExpense: RecyclerView
    private lateinit var transactionAdapter: DashboardTransactionAdapter
    private lateinit var lineChartMonthly: LineChart

    // Default to the current month and year
    private var selectedMonth =
        Calendar.getInstance().get(Calendar.MONTH)

    private var selectedYear =
        Calendar.getInstance().get(Calendar.YEAR)

    private var allTransactions: List<HistoryTransaction> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // INITIALIZE VIEWMODEL
        historyViewModel =
            ViewModelProvider(this)[HistoryViewModel::class.java]

        // INITIALIZE VIEWS
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance)
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome)
        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses)

        btnBack = view.findViewById(R.id.btnBack)
        spinnerMonth = view.findViewById(R.id.spinnerMonth)
        lineChartMonthly = view.findViewById(R.id.lineChartMonthly)
        setupMonthlyChart()
        recyclerViewExpense = view.findViewById(R.id.recyclerViewExpenses)

        // BACK TO MAIN SCREEN
        btnBack.setOnClickListener {
            requireActivity().finish()
        }

        // INITIALIZE MONTH SELECTOR
        setupMonthSelector()

        // INITIALIZE DASHBOARD ADAPTER
        transactionAdapter = DashboardTransactionAdapter(
            emptyList()
        ) { transaction ->
            openTransactionDetails(transaction)
        }

        recyclerViewExpense.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            isNestedScrollingEnabled = false
        }

        // OBSERVE ALL TRANSACTIONS
        historyViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->

            allTransactions = transactions
            updateDashboard()
        }
    }

    // SET UP THE MONTHLY INCOME AND EXPENSE CHART
    private fun setupMonthlyChart() {

        lineChartMonthly.apply {

            description.isEnabled = false

            setDrawGridBackground(false)
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)

            // DISABLE DATA POINT HIGHLIGHTING
            setHighlightPerTapEnabled(false)
            setHighlightPerDragEnabled(false)

            // Display message when there is no chart data
            setNoDataText("No transactions for this month")
            setNoDataTextColor(Color.WHITE)

            legend.textColor = Color.WHITE
            legend.textSize = 12f

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.LTGRAY
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                textColor = Color.LTGRAY
                setDrawGridLines(true)
                gridColor = Color.DKGRAY
                axisMinimum = 0f
            }

            axisRight.isEnabled = false

            setExtraOffsets(5f, 10f, 5f, 10f)
        }
    }

    // UPDATE MONTHLY CHART WITH DAILY INCOME AND EXPENSES
    private fun updateMonthlyChart(transactions: List<HistoryTransaction>) {

        // Get the number of days in the currently selected month
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Create daily lists, initially setting every day to zero
        val dailyIncome = MutableList(daysInMonth) { 0f }
        val dailyExpenses = MutableList(daysInMonth) { 0f }

        // Calculate the total income and expenses for each day
        transactions.forEach { transaction ->

            // Get the day of the month for this transaction
            val transactionCalendar = Calendar.getInstance().apply {
                timeInMillis = transaction.date
            }

            val dayIndex = transactionCalendar.get(Calendar.DAY_OF_MONTH) - 1

            // Add the transaction amount to the correct daily total
            if (dayIndex in 0 until daysInMonth) {

                when {
                    transaction.type.equals("Income", ignoreCase = true) -> {
                        dailyIncome[dayIndex] += transaction.amount.toFloat()
                    }

                    transaction.type.equals("Expense", ignoreCase = true) -> {
                        dailyExpenses[dayIndex] += transaction.amount.toFloat()
                    }
                }
            }
        }

        // Create chart entries for daily income
        val incomeEntries = dailyIncome.mapIndexed { index, amount ->
            Entry(index.toFloat(), amount)
        }

        // Create chart entries for daily expenses
        val expenseEntries = dailyExpenses.mapIndexed { index, amount ->
            Entry(index.toFloat(), amount)
        }

        // Configure the income line dataset
        val incomeDataSet = LineDataSet(
            incomeEntries,
            "Income"
        ).apply {

            // Set the income line colour to light blue
            color = Color.parseColor("#55D6E8")
            setCircleColor(Color.parseColor("#55D6E8"))

            lineWidth = 2.5f
            circleRadius = 3f
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR
        }

        // Configure the expense line dataset
        val expenseDataSet = LineDataSet(
            expenseEntries,
            "Expenses"
        ).apply {

            // Set the expense line colour to red
            color = Color.parseColor("#EF4444")
            setCircleColor(Color.parseColor("#EF4444"))

            lineWidth = 2.5f
            circleRadius = 3f
            setDrawCircleHole(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.LINEAR
        }

        // Combine both datasets into one chart
        lineChartMonthly.data = LineData(
            incomeDataSet,
            expenseDataSet
        )

        // Configure the horizontal axis to display calendar days
        lineChartMonthly.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(
                (1..daysInMonth).map { it.toString() }
            )

            axisMinimum = 0f
            axisMaximum = (daysInMonth - 1).toFloat()
            labelCount = 7
            granularity = 1f
        }

        // Refresh the chart to display the updated data
        lineChartMonthly.notifyDataSetChanged()
        lineChartMonthly.invalidate()
    }
    // SET UP MONTH DROPDOWN
    private fun setupMonthSelector() {

        val monthNames = java.text.DateFormatSymbols
            .getInstance(Locale.getDefault())
            .months
            .take(12)

        val monthAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            monthNames
        )

        monthAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerMonth.adapter = monthAdapter

        // Select current month by default
        spinnerMonth.setSelection(selectedMonth, false)

        spinnerMonth.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedMonth = position
                    updateDashboard()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                    // No action needed
                }
            }
    }

    // UPDATE DASHBOARD FOR SELECTED MONTH
    private fun updateDashboard() {

        val calendar = Calendar.getInstance()

        val selectedTransactions = allTransactions.filter { transaction ->

            calendar.timeInMillis = transaction.date

            val transactionMonth =
                calendar.get(Calendar.MONTH)

            val transactionYear =
                calendar.get(Calendar.YEAR)

            transactionMonth == selectedMonth &&
                    transactionYear == selectedYear
        }

        // CALCULATE MONTHLY INCOME
        val monthlyIncome = selectedTransactions
            .filter { it.type.equals("Income", ignoreCase = true) }
            .sumOf { it.amount }

        // CALCULATE MONTHLY EXPENSES
        val monthlyExpenses = selectedTransactions
            .filter { it.type.equals("Expense", ignoreCase = true) }
            .sumOf { it.amount }

        // UPDATE FINANCIAL SUMMARY
        updateFinancialSummary(monthlyIncome, monthlyExpenses)

        // UPDATE MONTHLY CHART WITH SELECTED MONTH'S TRANSACTIONS
        updateMonthlyChart(selectedTransactions)

        // SHOW FIVE MOST RECENT TRANSACTIONS FOR SELECTED MONTH
        val recentTransactions = selectedTransactions
            .sortedByDescending { it.date }
            .take(5)

        transactionAdapter.setTransactions(recentTransactions)
    }

    // UPDATE FINANCIAL SUMMARY
    private fun updateFinancialSummary(
        income: Double,
        expenses: Double
    ) {

        val balance = income - expenses

        tvTotalIncome.text = String.format(
            Locale.getDefault(),
            "R %,.2f",
            income
        )

        tvTotalExpenses.text = String.format(
            Locale.getDefault(),
            "R %,.2f",
            expenses
        )

        tvTotalBalance.text = String.format(
            Locale.getDefault(),
            "R %,.2f",
            balance
        )
    }

    // OPEN TRANSACTION DETAILS ONLY
    private fun openTransactionDetails(
        transaction: HistoryTransaction
    ) {

        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

        val intent = Intent(
            requireContext(),
            TransactionDetailsActivity::class.java
        ).apply {

            putExtra("TRANSACTION_ID", transaction.id)
            putExtra("TRANSACTION_TYPE", transaction.type)
            putExtra("TRANSACTION_TITLE", transaction.title)
            putExtra("TRANSACTION_AMOUNT", transaction.amount)
            putExtra("TRANSACTION_CATEGORY", transaction.category)

            putExtra(
                "TRANSACTION_DATE",
                dateFormat.format(Date(transaction.date))
            )

            putExtra(
                "TRANSACTION_DATE_MILLIS",
                transaction.date
            )
        }

        startActivity(intent)
    }
}
