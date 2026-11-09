package com.kate.expensetrackerclean

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.kate.expensetrackerclean.data.database.AppDatabase
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.database.IncomeEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateTransactionActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var tvTransactionType: TextView

    private lateinit var editTitle: EditText
    private lateinit var editAmount: EditText
    private lateinit var editCategory: EditText
    private lateinit var editDate: EditText

    private lateinit var tvErrorTitle: TextView
    private lateinit var tvErrorAmount: TextView
    private lateinit var tvErrorCategory: TextView
    private lateinit var tvErrorDate: TextView

    private lateinit var btnEditTransaction: MaterialButton
    private lateinit var btnSaveTransaction: MaterialButton

    private var isEditing = false

    // Existing transaction information
    private var transactionId = -1
    private var transactionType = ""
    private var transactionDateMillis = 0L

    private lateinit var database: AppDatabase

    private val dateFormat = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_update_transaction)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        initializeViews()

        database = AppDatabase.getDatabase(applicationContext)

        loadTransaction()

        setupBackButton()

        setupDatePicker()

        setupEditButton()

        setupSaveButton()

        setFieldsEnabled(false)
    }

    // INITIALIZE VIEWS
    private fun initializeViews() {

        btnBack = findViewById(R.id.btnBack)

        tvTransactionType = findViewById(R.id.tvTransactionType)

        editTitle = findViewById(R.id.editTitle)
        editAmount = findViewById(R.id.editAmount)
        editCategory = findViewById(R.id.editCategory)
        editDate = findViewById(R.id.editDate)

        tvErrorTitle = findViewById(R.id.tvErrorTitle)
        tvErrorAmount = findViewById(R.id.tvErrorAmount)
        tvErrorCategory = findViewById(R.id.tvErrorCategory)
        tvErrorDate = findViewById(R.id.tvErrorDate)

        btnEditTransaction = findViewById(R.id.btnEditTransaction)
        btnSaveTransaction = findViewById(R.id.btnSaveTransaction)
    }

    // LOAD EXISTING TRANSACTION
    private fun loadTransaction() {

        transactionId = intent.getIntExtra(
            "TRANSACTION_ID",
            -1
        )

        transactionType = intent.getStringExtra(
            "TRANSACTION_TYPE"
        ) ?: ""

        val title = intent.getStringExtra(
            "TRANSACTION_TITLE"
        ) ?: ""

        val amount = intent.getDoubleExtra(
            "TRANSACTION_AMOUNT",
            0.0
        )

        val category = intent.getStringExtra(
            "TRANSACTION_CATEGORY"
        ) ?: ""

        transactionDateMillis = intent.getLongExtra(
            "TRANSACTION_DATE_MILLIS",
            0L
        )

        if (transactionId == -1 || transactionDateMillis == 0L) {

            Toast.makeText(
                this,
                "Transaction information is missing",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        editTitle.setText(title)

        // ALWAYS DISPLAY TWO DECIMAL PLACES
        editAmount.setText(
            String.format(Locale.US, "%.2f", amount)
        )

        editCategory.setText(category)

        editDate.setText(
            dateFormat.format(transactionDateMillis)
        )

        tvTransactionType.text = transactionType
    }

    // BACK BUTTON
    private fun setupBackButton() {

        btnBack.setOnClickListener {
            finish()
        }
    }

    // DATE PICKER
    private fun setupDatePicker() {

        editDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            calendar.timeInMillis = transactionDateMillis

            val datePickerDialog = DatePickerDialog(
                this,

                { _, year, month, dayOfMonth ->

                    calendar.set(
                        year,
                        month,
                        dayOfMonth,
                        0,
                        0,
                        0
                    )

                    calendar.set(Calendar.MILLISECOND, 0)

                    transactionDateMillis = calendar.timeInMillis

                    editDate.setText(
                        dateFormat.format(calendar.time)
                    )

                    tvErrorDate.visibility = View.GONE
                },

                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }
    }

    // EDIT TRANSACTION BUTTON
    private fun setupEditButton() {

        btnEditTransaction.setOnClickListener {

            isEditing = true

            setFieldsEnabled(true)

            btnSaveTransaction.isEnabled = true

            btnEditTransaction.text = "Editing Transaction"

            Toast.makeText(
                this,
                "You can now edit the transaction",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ENABLE OR DISABLE FIELDS
    private fun setFieldsEnabled(enabled: Boolean) {

        editTitle.isEnabled = enabled
        editAmount.isEnabled = enabled
        editCategory.isEnabled = enabled

        editTitle.isFocusable = enabled
        editAmount.isFocusable = enabled
        editCategory.isFocusable = enabled

        editTitle.isFocusableInTouchMode = enabled
        editAmount.isFocusableInTouchMode = enabled
        editCategory.isFocusableInTouchMode = enabled

        editDate.isEnabled = enabled
        editDate.isFocusable = false
        editDate.isFocusableInTouchMode = false
        editDate.isClickable = enabled
    }

    // SAVE CHANGES
    private fun setupSaveButton() {

        btnSaveTransaction.setOnClickListener {

            if (!isEditing) {

                Toast.makeText(
                    this,
                    "Please press Edit Transaction first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (!validateInputs()) {
                return@setOnClickListener
            }

            updateTransaction()
        }
    }

    // UPDATE ROOM DATABASE
    private fun updateTransaction() {

        val title = editTitle.text.toString().trim()

        val amount = editAmount.text.toString().trim().toDouble()

        val category = editCategory.text.toString().trim()

        val date = transactionDateMillis

        btnSaveTransaction.isEnabled = false

        lifecycleScope.launch {

            try {

                when {

                    transactionType.equals("Income", ignoreCase = true) -> {

                        val income = IncomeEntity(
                            id = transactionId,
                            title = title,
                            amount = amount,
                            category = category,
                            date = date
                        )

                        database.incomeDao().updateIncome(income)
                    }

                    transactionType.equals("Expense", ignoreCase = true) -> {

                        val expense = ExpenseEntity(
                            id = transactionId,
                            title = title,
                            amount = amount,
                            category = category,
                            date = date
                        )

                        database.expenseDao().updateExpense(expense)
                    }

                    else -> {

                        Toast.makeText(
                            this@UpdateTransactionActivity,
                            "Unknown transaction type: $transactionType",
                            Toast.LENGTH_LONG
                        ).show()

                        btnSaveTransaction.isEnabled = true
                        return@launch
                    }
                }

                Toast.makeText(
                    this@UpdateTransactionActivity,
                    "Transaction updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                setResult(RESULT_OK)

                finish()

            } catch (e: Exception) {

                btnSaveTransaction.isEnabled = true

                Toast.makeText(
                    this@UpdateTransactionActivity,
                    "Update failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // VALIDATE INPUTS
    private fun validateInputs(): Boolean {

        var isValid = true

        val title = editTitle.text.toString().trim()

        val amount = editAmount.text.toString().trim()

        val category = editCategory.text.toString().trim()

        val date = editDate.text.toString().trim()

        // TITLE
        if (title.isEmpty()) {

            tvErrorTitle.text = "Please enter transaction title"

            tvErrorTitle.visibility = View.VISIBLE

            isValid = false

        } else {

            tvErrorTitle.visibility = View.GONE
        }

        // AMOUNT
        val amountValue = amount.toDoubleOrNull()

        if (amountValue == null || amountValue <= 0) {

            tvErrorAmount.text = "Please enter a valid amount"

            tvErrorAmount.visibility = View.VISIBLE

            isValid = false

        } else {

            tvErrorAmount.visibility = View.GONE
        }

        // CATEGORY
        if (category.isEmpty()) {

            tvErrorCategory.text = "Please enter transaction category"

            tvErrorCategory.visibility = View.VISIBLE

            isValid = false

        } else {

            tvErrorCategory.visibility = View.GONE
        }

        // DATE
        if (date.isEmpty()) {

            tvErrorDate.text = "Please select transaction date"

            tvErrorDate.visibility = View.VISIBLE

            isValid = false

        } else {

            tvErrorDate.visibility = View.GONE
        }

        return isValid
    }
}
