package com.kate.expensetrackerclean.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kate.expensetrackerclean.R
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import com.kate.expensetrackerclean.data.database.IncomeEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryTransaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val type: String
)

class HistoryTransactionAdapter(
    private var transactionList: List<HistoryTransaction>,
    private val onDetailsClick: (HistoryTransaction) -> Unit,
    private val onEditClick: (HistoryTransaction) -> Unit,
    private val onDeleteClick: (HistoryTransaction) -> Unit
) : RecyclerView.Adapter<HistoryTransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvAccount: TextView = itemView.findViewById(R.id.tvAccount)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        val btnDetails: Button = itemView.findViewById(R.id.btnDetails)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_transaction, parent, false)

        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TransactionViewHolder,
        position: Int
    ) {
        val transaction = transactionList[position]

        holder.tvTitle.text = transaction.title
        holder.tvCategory.text = transaction.category
        holder.tvAccount.text = transaction.type

        holder.tvDate.text = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(transaction.date))

        holder.tvAmount.text = String.format(
            Locale.getDefault(),
            "R %,.2f",
            transaction.amount
        )

        holder.btnDetails.setOnClickListener {
            onDetailsClick(transaction)
        }

        holder.btnEdit.setOnClickListener {
            onEditClick(transaction)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(transaction)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    fun setTransactions(transactions: List<HistoryTransaction>) {
        transactionList = transactions
        notifyDataSetChanged()
    }
}
