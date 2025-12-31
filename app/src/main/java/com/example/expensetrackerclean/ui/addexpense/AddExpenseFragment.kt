package com.example.expensetrackerclean.ui.addexpense

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.expensetrackerclean.R
import com.example.expensetrackerclean.data.database.ExpenseEntity
import com.google.android.material.button.MaterialButton
import com.example.expensetrackerclean.data.viewmodel.DashboardViewModel
import java.util.Calendar


class AddExpenseFragment: Fragment(R.layout.fragment_addexpense) {

    // ===== VIEWMODEL TO INTERACT WIT DASHBOARD =====
    private lateinit var dashboardViewModel: DashboardViewModel

    // ===== DECLARE VIEWS =====
    private lateinit var tvHeader: TextView
    private lateinit var editTitle: EditText
    private lateinit var editAmount: EditText
    private lateinit var editCategory: EditText
    private lateinit var editDate: EditText

    // ===== TEXT VIEWS TO SHOW VALIDATION ERRORS =====
    private lateinit var tvErrorTitle: TextView
    private lateinit var tvErrorAmount: TextView
    private lateinit var tvErrorCategory: TextView
    private lateinit var tvErrorDate: TextView

    // ===== BUTTONS =====
    private lateinit var btnSaveExpense: MaterialButton
    private lateinit var btnCancelExpense: MaterialButton

    private var selectedDate: Long = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== INITIALIZE VIEWMODEL =====
        dashboardViewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]

        // ===== INITIALIZE VIEWS =====
        tvHeader = view.findViewById(R.id.tvHeader)
        editTitle = view.findViewById(R.id.editTitle)
        editAmount = view.findViewById(R.id.editAmount)
        editCategory = view.findViewById(R.id.editCategory)
        editDate = view.findViewById(R.id.editDate)

        tvErrorTitle = view.findViewById(R.id.tvErrorTitle)
        tvErrorAmount = view.findViewById(R.id.tvErrorAmount)
        tvErrorCategory = view.findViewById(R.id.tvErrorCategory)
        tvErrorDate = view.findViewById(R.id.tvErrorDate)

        btnSaveExpense = view.findViewById(R.id.btnSaveExpense)
        btnCancelExpense = view.findViewById(R.id.btnCancelExpense)

        tvHeader.text = "Add New Expense"

        // ===== SETUP DATE PICKER =====
        setupDatePicker()
        // ===== SETUP SAVE & CANCEL BUTTON =====
        setupButtons()
    }

    // ===== FUNCTION TO SHOW CALENDER =====
    private fun setupDatePicker() {

        editDate.setOnClickListener {
            val cal = Calendar.getInstance()  // Get current date

            DatePickerDialog(
                requireContext(), { _, year, month, day ->
                    editDate.setText("$day/${month + 1}/$year")
                    tvErrorDate . visibility = View . GONE
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // ===== FUNCTION TO HANDLE BUTTONS =====
    private fun setupButtons() {

        // ===== SAVE BUTTON =====
        btnSaveExpense.setOnClickListener {

            val title = editTitle.text.toString().trim()
            val amountText = editAmount.text.toString().trim()
            val category = editCategory.text.toString().trim()
            val date = selectedDate

            var isValid = true   // Track if all fields are valid

            // ===== VALIDATION =====
            if (title.isEmpty()) {
                tvErrorTitle.text = "Title required"
                tvErrorTitle.visibility = View.VISIBLE
                isValid = false
            } else
                tvErrorTitle.visibility = View.GONE

            if (amountText.isEmpty()) {
                tvErrorAmount.text = "Amount required"
                tvErrorAmount.visibility = View.VISIBLE
                isValid = false
            } else
                tvErrorAmount.visibility = View.GONE

            if (category.isEmpty()) {
                tvErrorCategory.text = "Category required"
                tvErrorCategory.visibility = View.VISIBLE
                isValid = false
            } else
                tvErrorCategory.visibility = View.GONE

            if (date <= 0L) {
                tvErrorDate.text = "Date required"
                tvErrorDate.visibility = View.VISIBLE
                isValid = false
            } else
                tvErrorDate.visibility = View.GONE

            // ===== STOP SAVING IF ANY FIELD IS INVALID =====
            if (!isValid)
                return@setOnClickListener

            // ===== CREATE A NEW EXPENSE OBJECT =====
            val expense = ExpenseEntity(
                title = title,
                amount = amountText.toDouble(),
                category = category,
                date = date
            )
            // ===== ADD EXPENSE USING VIEWMODEL =====
            dashboardViewModel.insertExpense(expense)
            Toast.makeText(requireContext(), "Expense added", Toast.LENGTH_SHORT).show()

            // ===== NAVIGATE BACK TO DASHBOARD =====
            findNavController().navigateUp()
        }
        // ===== CANCEL BUTTON  =====
        btnCancelExpense.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}








