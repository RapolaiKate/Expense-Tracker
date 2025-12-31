package com.example.expensetrackerclean.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetrackerclean.R
import com.example.expensetrackerclean.data.viewmodel.DashboardViewModel
import com.google.android.material.button.MaterialButton
import java.util.Calendar


class StatsFragment: Fragment() {

    // ===== SHARE VIEWMODEL WITH DASHBOARD =====
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    // ===== TEXT VIEWS =====
    private lateinit var tvStatsTitle: TextView
    private lateinit var tvTotalAmountLabel: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvMonthlyAmountLabel: TextView
    private lateinit var tvMonthlyAmount: TextView
    private lateinit var btnBack: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_stats, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== INITIALIZE TEXT VIEWS =====
        tvStatsTitle = view.findViewById(R.id.tvStatsTitle)
        tvTotalAmountLabel = view.findViewById(R.id.tvTotalAmountLabel)
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        tvMonthlyAmountLabel = view.findViewById(R.id.tvTotalAmountLabel)
        tvMonthlyAmount = view.findViewById(R.id.tvMonthlyAmount)
        btnBack = view.findViewById(R.id.btnBack)

        // ===== OBSERVE EXPENSES =====
        observeStats()

    }

    private fun observeStats() {

        // ====== TOTAL AMOUNT =====
        dashboardViewModel.totalAmount.observe(viewLifecycleOwner) {

                total ->
            tvTotalAmount.text = "R %.2f".format(total)
        }
        // ===== MONTHLY TOTAL =====
        dashboardViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->

            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            val monthlyTotal = expenses.filter { expenses ->

                val cal = Calendar.getInstance()
                cal.timeInMillis = expenses.date
                cal.get(Calendar.MONTH) == currentMonth &&
                        cal.get(Calendar.YEAR) == currentYear
            }.sumOf { it.amount }
            tvMonthlyAmount.text = "R %.2f".format(monthlyTotal)
        }
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}







