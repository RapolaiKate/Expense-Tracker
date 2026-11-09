package com.kate.expensetrackerclean.ui.stats

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.kate.expensetrackerclean.LoginActivity
import com.kate.expensetrackerclean.R
import com.kate.expensetrackerclean.data.database.AppDatabase
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import com.kate.expensetrackerclean.data.repository.IncomeRepository
import com.kate.expensetrackerclean.data.viewmodel.StatsViewModel
import com.kate.expensetrackerclean.data.viewmodel.StatsViewModelFactory
import java.util.Locale

class StatsFragment : Fragment() {

    // =====================================
    // UI ELEMENTS
    // =====================================

    private lateinit var barChart: BarChart
    private lateinit var tvStatsTitle: TextView
    private lateinit var tvTotalAmountLabel: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvMonthlyAmountLabel: TextView
    private lateinit var tvMonthlyAmount: TextView

    // Identify the selected statistics screen
    private val isIncomeScreen: Boolean
        get() = arguments?.getString("transaction_type") == "INCOME"


    // =====================================
    // LOAD CORRECT XML LAYOUT
    // =====================================

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val layoutId = if (isIncomeScreen) {
            R.layout.fragment_income_stats
        } else {
            R.layout.fragment_expense_stats
        }

        return inflater.inflate(layoutId, container, false)
    }


    // =====================================
    // INITIALIZE SCREEN
    // =====================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Connect common views
        tvStatsTitle = view.findViewById(R.id.tvStatsTitle)

        // Connect Income or Expense views
        if (isIncomeScreen) {

            tvTotalAmountLabel =
                view.findViewById(R.id.tvTotalIncomeLabel)

            tvTotalAmount =
                view.findViewById(R.id.tvTotalIncome)

            tvMonthlyAmountLabel =
                view.findViewById(R.id.tvMonthlyIncomeLabel)

            tvMonthlyAmount =
                view.findViewById(R.id.tvMonthlyIncome)

            barChart =
                view.findViewById(R.id.barChartIncome)

        } else {

            tvTotalAmountLabel =
                view.findViewById(R.id.tvTotalAmountLabel)

            tvTotalAmount =
                view.findViewById(R.id.tvTotalAmount)

            tvMonthlyAmountLabel =
                view.findViewById(R.id.tvMonthlyAmountLabel)

            tvMonthlyAmount =
                view.findViewById(R.id.tvMonthlyAmount)

            barChart =
                view.findViewById(R.id.barChart)
        }

        // Set screen headings
        if (isIncomeScreen) {

            tvStatsTitle.text = "Income Statistics"
            tvTotalAmountLabel.text = "Total Income"
            tvMonthlyAmountLabel.text = "Monthly Income"

        } else {

            tvStatsTitle.text = "Expense Statistics"
            tvTotalAmountLabel.text = "Total Expenses"
            tvMonthlyAmountLabel.text = "Monthly Expenses"
        }


        // =====================================
        // CONNECT LOGOUT BUTTON
        // =====================================

        val btnLogout = if (isIncomeScreen) {
            view.findViewById<View>(R.id.btnLogoutIncome)
        } else {
            view.findViewById<View>(R.id.btnLogoutExpense)
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }


        // =====================================
        // CREATE DATABASE REPOSITORIES
        // =====================================

        val database =
            AppDatabase.getDatabase(requireContext())

        val expenseRepository =
            ExpenseRepository(database.expenseDao())

        val incomeRepository =
            IncomeRepository(database.incomeDao())


        // =====================================
        // CREATE STATISTICS VIEWMODEL
        // =====================================

        val factory = StatsViewModelFactory(
            expenseRepository,
            incomeRepository
        )

        val viewModel = ViewModelProvider(
            this,
            factory
        )[StatsViewModel::class.java]


        // =====================================
        // OBSERVE INCOME STATISTICS
        // =====================================

        if (isIncomeScreen) {

            viewModel.totalIncome.observe(viewLifecycleOwner) {
                tvTotalAmount.text = formatCurrency(it)
            }

            viewModel.monthlyIncome.observe(viewLifecycleOwner) {
                tvMonthlyAmount.text = formatCurrency(it)
            }

            viewModel.allIncome.observe(viewLifecycleOwner) { incomeList ->

                val categoryTotals = incomeList
                    .groupBy { it.category }
                    .map { entry ->
                        entry.key to entry.value.sumOf { it.amount }
                    }

                updateChart(
                    categoryTotals,
                    "Income by Category",
                    true
                )
            }

        } else {


            // =====================================
            // OBSERVE EXPENSE STATISTICS
            // =====================================

            viewModel.totalExpenses.observe(viewLifecycleOwner) {
                tvTotalAmount.text = formatCurrency(it)
            }

            viewModel.monthlyExpenses.observe(viewLifecycleOwner) {
                tvMonthlyAmount.text = formatCurrency(it)
            }

            viewModel.allExpenses.observe(viewLifecycleOwner) { expenseList ->

                val categoryTotals = expenseList
                    .groupBy { it.category }
                    .map { entry ->
                        entry.key to entry.value.sumOf { it.amount }
                    }

                updateChart(
                    categoryTotals,
                    "Expenses by Category",
                    false
                )
            }
        }
    }


    // =====================================
    // LOGOUT USER
    // =====================================

    private fun logoutUser() {

        // Sign out the current Firebase user
        FirebaseAuth.getInstance().signOut()

        // Open LoginActivity
        val intent = Intent(
            requireContext(),
            LoginActivity::class.java
        )

        // Clear the previous authenticated screens
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        requireActivity().finish()
    }


    // =====================================
    // FORMAT CURRENCY
    // =====================================

    private fun formatCurrency(amount: Double?): String {

        return String.format(
            Locale("en", "ZA"),
            "R %.2f",
            amount ?: 0.0
        )
    }


    // =====================================
    // UPDATE BAR CHART
    // =====================================

    private fun updateChart(
        categoryTotals: List<Pair<String, Double>>,
        chartLabel: String,
        isIncome: Boolean
    ) {

        val textColor = ContextCompat.getColor(
            requireContext(),
            R.color.on_surface
        )

        // Handle empty transaction list
        if (categoryTotals.isEmpty()) {

            barChart.clear()

            barChart.setNoDataText(
                "No transactions recorded yet"
            )

            barChart.setNoDataTextColor(textColor)

            barChart.invalidate()

            return
        }


        // Prepare chart entries and category labels
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        categoryTotals.forEachIndexed { index, item ->

            labels.add(item.first)

            entries.add(
                BarEntry(
                    index.toFloat(),
                    item.second.toFloat()
                )
            )
        }


        // Create chart data set
        val dataSet = BarDataSet(
            entries,
            chartLabel
        )


        // Set Income and Expense bar colours
        dataSet.color = if (isIncome) {
            Color.parseColor("#00C853")
        } else {
            Color.parseColor("#EF4444")
        }

        dataSet.valueTextColor = textColor
        dataSet.valueTextSize = 11f


        // Configure chart data
        val barData = BarData(dataSet)

        barData.barWidth = 0.6f

        barChart.data = barData


        // Configure X Axis
        val xAxis = barChart.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = textColor
        xAxis.textSize = 10f
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)


        // Configure Y Axis
        barChart.axisLeft.textColor = textColor
        barChart.axisRight.isEnabled = false


        // Configure chart appearance
        barChart.description.isEnabled = false
        barChart.legend.textColor = textColor

        barChart.setFitBars(true)
        barChart.animateY(700)

        barChart.invalidate()
    }
}
