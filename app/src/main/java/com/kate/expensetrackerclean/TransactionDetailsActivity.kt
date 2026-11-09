package com.kate.expensetrackerclean

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TransactionDetailsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView

    private lateinit var tvTransactionType: TextView
    private lateinit var tvTransactionAmount: TextView
    private lateinit var tvTransactionCategory: TextView
    private lateinit var tvTransactionDate: TextView
    private lateinit var tvTransactionDescription: TextView

    private lateinit var btnEditTransaction: Button
    private lateinit var btnDeleteTransaction: Button

    private var transactionId = 0
    private var transactionType = "Expense"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transaction_details)

        initializeViews()
        loadTransactionDetails()
        setupButtons()
    }

    private fun initializeViews() {

        btnBack = findViewById(R.id.btnBack)

        tvTransactionType =
            findViewById(R.id.tvTransactionType)

        tvTransactionAmount =
            findViewById(R.id.tvTransactionAmount)

        tvTransactionCategory =
            findViewById(R.id.tvTransactionCategory)

        tvTransactionDate =
            findViewById(R.id.tvTransactionDate)

        tvTransactionDescription =
            findViewById(R.id.tvTransactionDescription)

        btnEditTransaction =
            findViewById(R.id.btnEditTransaction)

        btnDeleteTransaction =
            findViewById(R.id.btnDeleteTransaction)
    }

    private fun loadTransactionDetails() {

        transactionId = intent.getIntExtra(
            "TRANSACTION_ID", -1
        )

        transactionType = intent.getStringExtra(
            "TRANSACTION_TYPE"
        ) ?: "Expense"

        val title = intent.getStringExtra(
            "TRANSACTION_TITLE"
        ) ?: "Unknown Transaction"

        val amount = intent.getDoubleExtra(
            "TRANSACTION_AMOUNT", 0.0
        )

        val category = intent.getStringExtra(
            "TRANSACTION_CATEGORY"
        ) ?: "Unknown Category"

        val date = intent.getStringExtra(
            "TRANSACTION_DATE"
        ) ?: "Unknown Date"

        tvTransactionType.text = transactionType

        tvTransactionAmount.text = String.format(
            java.util.Locale.getDefault(),
            "R %,.2f",
            amount
        )

        tvTransactionCategory.text = category
        tvTransactionDate.text = date

        // The entity contains a title, not a description.
        tvTransactionDescription.text = title

        if (transactionType.equals("Income", ignoreCase = true)) {
            tvTransactionAmount.setTextColor(
                Color.parseColor("#00C853")
            )
        } else {
            tvTransactionAmount.setTextColor(
                Color.parseColor("#FF5252")
            )
        }
    }

    private fun setupButtons() {

        btnBack.setOnClickListener {
            finish()
        }

        btnEditTransaction.setOnClickListener {
            openNextScreen(UpdateTransactionActivity::class.java)
        }

        btnDeleteTransaction.setOnClickListener {
            openNextScreen(DeleteTransactionActivity::class.java)
        }
    }

    private fun openNextScreen(
        activityClass: Class<*>
    ) {

        val nextIntent = Intent(this, activityClass).apply {

            putExtra("TRANSACTION_ID", transactionId)

            putExtra("TRANSACTION_TYPE", transactionType)

            putExtra(
                "TRANSACTION_TITLE",
                tvTransactionDescription.text.toString()
            )

            putExtra(
                "TRANSACTION_AMOUNT",
                intent.getDoubleExtra("TRANSACTION_AMOUNT", 0.0)
            )

            putExtra(
                "TRANSACTION_CATEGORY",
                tvTransactionCategory.text.toString()
            )

            putExtra(
                "TRANSACTION_DATE",
                tvTransactionDate.text.toString()
            )

            putExtra(
                "TRANSACTION_DATE_MILLIS",
                intent.getLongExtra("TRANSACTION_DATE_MILLIS", 0L)
            )
        }

        startActivity(nextIntent)
    }
}
