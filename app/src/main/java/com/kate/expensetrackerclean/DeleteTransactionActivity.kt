package com.kate.expensetrackerclean

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.kate.expensetrackerclean.data.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import java.util.Locale

class DeleteTransactionActivity : AppCompatActivity() {

    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var tvDeleteTransactionType: TextView
    private lateinit var tvDeleteTitle: TextView
    private lateinit var tvDeleteAmount: TextView
    private lateinit var tvDeleteCategory: TextView
    private lateinit var tvDeleteDate: TextView

    private lateinit var btnConfirmDelete: MaterialButton
    private lateinit var btnCancelDelete: MaterialButton

    private var transactionId: String? = null
    private var transactionType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_transaction)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

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

        historyViewModel =
            ViewModelProvider(this)[HistoryViewModel::class.java]

        initializeViews()
        setupBackButton()
        loadTransactionDetails()
        setupCancelButton()
        setupDeleteButton()
    }

    private fun initializeViews() {

        tvDeleteTransactionType =
            findViewById(R.id.tvDeleteTransactionType)

        tvDeleteTitle =
            findViewById(R.id.tvDeleteTitle)

        tvDeleteAmount =
            findViewById(R.id.tvDeleteAmount)

        tvDeleteCategory =
            findViewById(R.id.tvDeleteCategory)

        tvDeleteDate =
            findViewById(R.id.tvDeleteDate)

        btnConfirmDelete =
            findViewById(R.id.btnConfirmDelete)

        btnCancelDelete =
            findViewById(R.id.btnCancelDelete)
    }

    private fun setupBackButton() {

        findViewById<android.widget.ImageView>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }
    }

    private fun loadTransactionDetails() {

        transactionId =
            intent.extras?.get("TRANSACTION_ID")?.toString()

        transactionType =
            intent.getStringExtra("TRANSACTION_TYPE")

        val title =
            intent.getStringExtra("TRANSACTION_TITLE")
                ?: "Unknown Transaction"

        val amount =
            intent.getDoubleExtra("TRANSACTION_AMOUNT", 0.0)

        val category =
            intent.getStringExtra("TRANSACTION_CATEGORY")
                ?: "Unknown Category"

        val date =
            intent.getStringExtra("TRANSACTION_DATE")
                ?: "Unknown Date"

        tvDeleteTransactionType.text =
            transactionType ?: "Unknown Type"

        tvDeleteTitle.text = title

        tvDeleteAmount.text = String.format(
            Locale.getDefault(),
            "R %.2f",
            amount
        )

        tvDeleteCategory.text = category

        tvDeleteDate.text = date

        if (transactionType.equals("Income", ignoreCase = true)) {

            tvDeleteAmount.setTextColor(
                android.graphics.Color.parseColor("#00C853")
            )

        } else {

            tvDeleteAmount.setTextColor(
                android.graphics.Color.parseColor("#FF5252")
            )
        }
    }

    private fun setupCancelButton() {

        btnCancelDelete.setOnClickListener {
            finish()
        }
    }

    private fun setupDeleteButton() {

        btnConfirmDelete.setOnClickListener {

            if (transactionId.isNullOrBlank()) {

                Toast.makeText(
                    this,
                    "Transaction ID is missing.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            if (
                transactionType != "Income" &&
                transactionType != "Expense"
            ) {

                Toast.makeText(
                    this,
                    "Invalid transaction type.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {

        AlertDialog.Builder(this)

            .setTitle("Delete Transaction?")

            .setMessage(
                "Are you sure you want to delete this transaction? " +
                        "This action cannot be undone."
            )

            .setPositiveButton("Delete") { _, _ ->

                deleteTransaction()

            }

            .setNegativeButton("Cancel", null)

            .show()
    }

    private fun deleteTransaction() {

        val id = transactionId ?: return
        val type = transactionType ?: return

        btnConfirmDelete.isEnabled = false

        lifecycleScope.launch {

            try {

                val deletedRows =
                    historyViewModel.deleteTransactionById(
                        id,
                        type
                    )

                if (deletedRows > 0) {

                    Toast.makeText(
                        this@DeleteTransactionActivity,
                        "$type transaction deleted successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    btnConfirmDelete.isEnabled = true

                    Toast.makeText(
                        this@DeleteTransactionActivity,
                        "Transaction not found. It may already have been deleted.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {

                btnConfirmDelete.isEnabled = true

                Toast.makeText(
                    this@DeleteTransactionActivity,
                    "Unable to delete transaction: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
