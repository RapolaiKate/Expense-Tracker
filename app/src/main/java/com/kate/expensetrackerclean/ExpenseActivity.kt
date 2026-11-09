package com.kate.expensetrackerclean

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.viewmodel.AddExpenseViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import android.view.ContextThemeWrapper
import java.util.Locale

class ExpenseActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etExpenseAmount: TextInputEditText
    private lateinit var tvExpenseCategory: MaterialAutoCompleteTextView
    private lateinit var etExpenseDate: TextInputEditText
    private lateinit var etExpenseDescription: TextInputEditText
    private lateinit var btnSaveExpense: MaterialButton

    private lateinit var tilExpenseCategory: TextInputLayout
    private lateinit var tilExpenseDate: TextInputLayout

    private lateinit var expenseViewModel: AddExpenseViewModel

    private val expenseCategories = arrayOf(
        "Food & Groceries",
        "Transport",
        "Housing & Rent",
        "Utilities",
        "Shopping",
        "Healthcare",
        "Education",
        "Entertainment",
        "Personal Care",
        "Debt & Loans",
        "Other"
    )

    private val calendar = Calendar.getInstance()

    private val dateFormat =
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        // Initialize ViewModel
        expenseViewModel =
            ViewModelProvider(this)[AddExpenseViewModel::class.java]

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        etExpenseAmount = findViewById(R.id.etExpenseAmount)
        tvExpenseCategory = findViewById(R.id.tvExpenseCategory)
        etExpenseDate = findViewById(R.id.etExpenseDate)
        etExpenseDescription = findViewById(R.id.etExpenseDescription)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)

        tilExpenseCategory = findViewById(R.id.tilExpenseCategory)
        tilExpenseDate = findViewById(R.id.tilExpenseDate)

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Set up category dropdown
        setupCategoryDropdown()

        // Set today's date
        updateDateField()

        // Open date picker
        etExpenseDate.setOnClickListener {
            showDatePicker()
        }

        tilExpenseDate.setEndIconOnClickListener {
            showDatePicker()
        }

        // Save expense
        btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun setupCategoryDropdown() {

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            expenseCategories
        )

        tvExpenseCategory.setAdapter(adapter)

        tvExpenseCategory.setOnItemClickListener { _, _, _, _ ->
            tilExpenseCategory.error = null
        }
    }

    private fun showDatePicker() {

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            ContextThemeWrapper(this, R.style.IncomeExpenseDatePickerTheme),
            { _, selectedYear, selectedMonth, selectedDay ->

                calendar.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                    0,
                    0,
                    0
                )

                calendar.set(Calendar.MILLISECOND, 0)

                updateDateField()
            },
            year,
            month,
            day
        )

        // Prevent future dates
        datePickerDialog.datePicker.maxDate =
            System.currentTimeMillis()

        datePickerDialog.show()

        datePickerDialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.parseColor("#15151D"))
        )
    }

    private fun updateDateField() {

        etExpenseDate.setText(
            dateFormat.format(calendar.time)
        )
    }

    private fun saveExpense() {

        val amountText =
            etExpenseAmount.text.toString().trim()

        val category =
            tvExpenseCategory.text.toString().trim()

        val date =
            etExpenseDate.text.toString().trim()

        val description =
            etExpenseDescription.text.toString().trim()

        // Validate amount
        if (amountText.isEmpty()) {

            etExpenseAmount.error =
                "Please enter expense amount"

            etExpenseAmount.requestFocus()
            return
        }

        val amount = amountText.toDoubleOrNull()

        if (amount == null || amount <= 0 || !amount.isFinite()) {

            etExpenseAmount.error =
                "Enter a valid amount"

            etExpenseAmount.requestFocus()
            return
        }

        // Validate category
        if (category.isEmpty() ||
            !expenseCategories.contains(category)
        ) {

            tilExpenseCategory.error =
                "Please select an expense category"

            return
        }

        tilExpenseCategory.error = null

        // Validate date
        if (date.isEmpty()) {

            tilExpenseDate.error =
                "Please select a date"

            return
        }

        tilExpenseDate.error = null

        // Create Room expense transaction
        val expense = ExpenseEntity(
            title = description.ifEmpty { category },
            amount = amount,
            category = category,
            date = calendar.timeInMillis
        )

        // Save expense into Room database
        expenseViewModel.insertExpense(expense)

        Toast.makeText(
            this,
            "Expense saved successfully!",
            Toast.LENGTH_SHORT
        ).show()

        // Clear form
        etExpenseAmount.text?.clear()
        tvExpenseCategory.setText("", false)
        etExpenseDescription.text?.clear()

        // Reset date to today
        calendar.time = Calendar.getInstance().time

        updateDateField()
    }
}