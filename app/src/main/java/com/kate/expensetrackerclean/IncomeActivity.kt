package com.kate.expensetrackerclean

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kate.expensetrackerclean.data.database.IncomeEntity
import com.kate.expensetrackerclean.data.viewmodel.AddIncomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import android.view.ContextThemeWrapper
import java.util.Locale

class IncomeActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etIncomeAmount: EditText
    private lateinit var tvIncomeCategory: MaterialAutoCompleteTextView
    private lateinit var etIncomeDate: TextInputEditText
    private lateinit var etIncomeDescription: TextInputEditText
    private lateinit var btnSaveIncome: MaterialButton

    private lateinit var tilIncomeCategory: TextInputLayout
    private lateinit var tilIncomeDate: TextInputLayout

    private lateinit var incomeViewModel: AddIncomeViewModel

    private var selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_income)

        // INITIALIZE VIEWMODEL
        incomeViewModel = ViewModelProvider(this)
            .get(AddIncomeViewModel::class.java)

        // INITIALIZE VIEWS
        btnBack = findViewById(R.id.btnBack)
        etIncomeAmount = findViewById(R.id.etIncomeAmount)
        tvIncomeCategory = findViewById(R.id.tvIncomeCategory)
        etIncomeDate = findViewById(R.id.etIncomeDate)
        etIncomeDescription = findViewById(R.id.etIncomeDescription)
        btnSaveIncome = findViewById(R.id.btnSaveIncome)

        tilIncomeCategory = findViewById(R.id.tilIncomeCategory)
        tilIncomeDate = findViewById(R.id.tilIncomeDate)

        // BACK BUTTON
        btnBack.setOnClickListener {
            finish()
        }

        // INCOME CATEGORIES
        val incomeCategories = arrayOf(
            "Salary",
            "Freelance",
            "Business",
            "Investment",
            "Gift",
            "Interest",
            "Rental Income",
            "Bonus",
            "Other"
        )

        // CATEGORY DROPDOWN
        val categoryAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            incomeCategories
        ) {

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view = super.getView(
                    position,
                    convertView,
                    parent
                )

                (view as TextView).setTextColor(Color.WHITE)

                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view = super.getDropDownView(
                    position,
                    convertView,
                    parent
                )

                (view as TextView).setTextColor(Color.WHITE)

                return view
            }
        }

        tvIncomeCategory.setAdapter(categoryAdapter)

        tvIncomeCategory.setDropDownBackgroundDrawable(
            ColorDrawable(Color.parseColor("#2B2B35"))
        )

        tvIncomeCategory.setOnClickListener {
            tvIncomeCategory.showDropDown()
        }

        tvIncomeCategory.setOnItemClickListener { _, _, _, _ ->
            tilIncomeCategory.error = null
        }

        // SET TODAY'S DATE
        updateDateField()

        // DATE PICKER
        etIncomeDate.setOnClickListener {
            showDatePicker()
        }

        tilIncomeDate.setEndIconOnClickListener {
            showDatePicker()
        }

        // SAVE INCOME
        btnSaveIncome.setOnClickListener {
            saveIncome()
        }
    }

    // DISPLAY SELECTED DATE
    private fun updateDateField() {

        val dateFormat = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

        etIncomeDate.setText(
            dateFormat.format(selectedDate.time)
        )
    }

    // OPEN CALENDAR
    private fun showDatePicker() {

        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            ContextThemeWrapper(this, R.style.IncomeExpenseDatePickerTheme),
            { _, selectedYear, selectedMonth, selectedDay ->

                selectedDate.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay
                )

                // RESET TIME TO MIDNIGHT
                selectedDate.set(
                    Calendar.HOUR_OF_DAY,
                    0
                )
                selectedDate.set(Calendar.MINUTE, 0)
                selectedDate.set(Calendar.SECOND, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)

                updateDateField()

                tilIncomeDate.error = null
            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    // VALIDATE AND SAVE INCOME
    private fun saveIncome() {

        val amountText =
            etIncomeAmount.text.toString().trim()

        val category =
            tvIncomeCategory.text.toString().trim()

        val description =
            etIncomeDescription.text.toString().trim()

        val date =
            etIncomeDate.text.toString().trim()

        // VALIDATE AMOUNT
        if (amountText.isEmpty()) {

            etIncomeAmount.error =
                "Please enter an amount"

            etIncomeAmount.requestFocus()

            return
        }

        val amount = amountText.toDoubleOrNull()

        if (amount == null || !amount.isFinite() || amount <= 0) {

            etIncomeAmount.error =
                "Enter a valid amount greater than zero"

            etIncomeAmount.requestFocus()

            return
        }

        // VALIDATE CATEGORY
        val incomeCategories = arrayOf(
            "Salary",
            "Freelance",
            "Business",
            "Investment",
            "Gift",
            "Interest",
            "Rental Income",
            "Bonus",
            "Other"
        )

        if (category !in incomeCategories) {

            tilIncomeCategory.error =
                "Please select an income category"

            tvIncomeCategory.requestFocus()

            return
        }

        tilIncomeCategory.error = null

        // VALIDATE DATE
        if (date.isEmpty()) {

            tilIncomeDate.error =
                "Please select a date"

            return
        }

        tilIncomeDate.error = null

        // CREATE INCOME ENTITY
        val income = IncomeEntity(
            title = description.ifEmpty {
                category
            },
            amount = amount,
            category = category,
            date = selectedDate.timeInMillis
        )

        // SAVE THROUGH VIEWMODEL AND ROOM
        incomeViewModel.insertIncome(income)

        Toast.makeText(
            this,
            "Income saved successfully!",
            Toast.LENGTH_SHORT
        ).show()

        // CLEAR FORM
        etIncomeAmount.text.clear()

        tvIncomeCategory.setText("", false)

        etIncomeDescription.text?.clear()

        selectedDate = Calendar.getInstance()

        updateDateField()

        tilIncomeCategory.error = null
        tilIncomeDate.error = null
    }
}
