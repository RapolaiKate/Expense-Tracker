package com.example.expensetrackerclean.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.example.expensetrackerclean.R
import com.example.expensetrackerclean.data.viewmodel.DashboardViewModel
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment: Fragment(R.layout.fragment_dashboard) {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var tvTotalAmount: TextView
    private lateinit var recyclerViewExpense: RecyclerView
    private lateinit var btnAddExpense: MaterialButton
    private lateinit var btnStats: MaterialButton
    private lateinit var expenseAdapter: ItemExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ===== INFLATE THE FRAGMENT LAYOUT =====
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // =====  INITIALIZE VIEWMODEL =====
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        // ===== INITIALIZE VIEWS =====
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        recyclerViewExpense = view.findViewById(R.id.recyclerViewExpenses)
        btnAddExpense = view.findViewById(R.id.btnAddExpense)
        btnStats = view.findViewById(R.id.btnStats)

        // ===== INITIALIZE ADAPTER =====
        expenseAdapter = ItemExpenseAdapter(emptyList())

        recyclerViewExpense.apply {

            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }

        // ===== OBSERVE ALL EXPENSE =====
        dashboardViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter.setExpenses(expenses)
        }
        // ===== OBSERVE TOTAL AMOUNT =====
        dashboardViewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            val safeTotal = total ?: 0.0
            tvTotalAmount.text = String.format("R %.2f", safeTotal)
        }
        // ===== ADD BUTTON =====
        btnAddExpense.setOnClickListener {

            // ===== NAVIGATE TO ADD EXPENSE FRAGMENT =====
            val action = DashboardFragmentDirections.actionDashboardFragmentToAddExpenseFragment()
            findNavController().navigate(action)
        }
        btnStats.setOnClickListener {

            // ===== NAVIGATE TO STATS FRAGMENT =====
            val action = DashboardFragmentDirections.actionDashboardFragmentToStatsFragment()
            findNavController().navigate(action)
        }
    }
}
