package com.kate.expensetrackerclean.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.kate.expensetrackerclean.R
import com.google.android.material.button.MaterialButton
import com.kate.expensetrackerclean.data.database.ExpenseDatabase
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.repository.ExpenseRepository
import com.kate.expensetrackerclean.data.viewmodel.StatsViewModel
import com.kate.expensetrackerclean.data.viewmodel.StatsViewModelFactory
import kotlin.collections.forEachIndexed


class StatsFragment: Fragment() {

    // ===== TEXT VIEWS =====
    private lateinit var barChart: BarChart
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvMonthlyAmount: TextView
    private lateinit var btnBack: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== INITIALIZE TEXT VIEWS =====
        barChart = view.findViewById(R.id.barChart)
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        tvMonthlyAmount = view.findViewById(R.id.tvMonthlyAmount)
        btnBack = view.findViewById(R.id.btnBack)

        // ===== GET REPOSITORY INSTANCE =====
        val repository = ExpenseRepository(ExpenseDatabase.getDatabase(requireContext()).expenseDao())

        // ===== USE FACTORY TO GET VIEWMODEL =====
        val viewModel = ViewModelProvider(this,StatsViewModelFactory(repository)).get(StatsViewModel::class.java)

        // ===== OBSERVE DATA FROM ROOM =====
        viewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->

            if (barChart == null || tvTotalAmount == null || tvMonthlyAmount == null)
                return@observe

            if(expenses.isEmpty()) {
                tvTotalAmount.text = "0.00"
                tvMonthlyAmount.text = "0.00"
                barChart.clear()
                barChart.invalidate()
                return@observe
            }
            // ===== TOTAL EXPENSES =====
            val total = expenses.sumOf { it.amount }
            tvTotalAmount.text = total.toString()
            // ===== MONTHLY EXPENSES =====
            val monthly = expenses.sumOf { it.amount }
            tvMonthlyAmount.text = monthly.toString()

            updateChart(barChart, expenses)
        }
    }
    // ===== CHART WITH REAL DATA =====
    private fun updateChart(barChart: BarChart, expenses: List<ExpenseEntity>) {
        val entries = ArrayList<BarEntry>()

        expenses.forEachIndexed { index, expense ->
            entries.add(BarEntry(index.toFloat(),
                expense.amount.toFloat()))
        }
        // ===== BAR COLORS =====
        val dataSet = BarDataSet(entries, "Monthly Expenses")
        val textColor = ContextCompat.getColor(requireContext(), R.color.on_surface)
        barChart.setNoDataTextColor(textColor)
        barChart.description.textColor = textColor
        barChart.legend.textColor = textColor

        dataSet.valueTextColor = textColor
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.invalidate()    // refresh chart

        // ===== X-AXIS LABELS =====
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = textColor
        xAxis.textSize = 12f
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Toiletries", "Rent", "Lunch", "Shopping", "Grocery"))

        // ===== Y-AXIS NUMBERS =====
        barChart.axisLeft.textColor = textColor
        barChart.axisRight.textColor = textColor

        btnBack.setOnClickListener {

            // ===== GO BACK TO PREVIOUS SCREEN =====
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}







