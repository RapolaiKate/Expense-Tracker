package com.kate.expensetrackerclean

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kate.expensetrackerclean.ui.stats.StatsFragment

class StatsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRANSACTION_TYPE = "transaction_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_stats)

        if (savedInstanceState == null) {
            val transactionType =
                intent.getStringExtra(EXTRA_TRANSACTION_TYPE) ?: "EXPENSE"

            val fragment = StatsFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_TRANSACTION_TYPE, transactionType)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.statsFragmentContainer, fragment)
                .commit()
        }
    }
}