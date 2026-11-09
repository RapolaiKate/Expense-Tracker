package com.kate.expensetrackerclean.ui.dashboard

import androidx.recyclerview.widget.RecyclerView
import com.kate.expensetrackerclean.data.database.ExpenseEntity
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import androidx.navigation.findNavController
import com.kate.expensetrackerclean.R
import com.kate.expensetrackerclean.utils.formateDate

class ItemExpenseAdapter(private var expenseList: List<ExpenseEntity>): RecyclerView.Adapter<ItemExpenseAdapter.ExpenseViewHolder>() {

     class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_transaction, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {

        val expense = expenseList[position]
        holder.tvTitle.text = expense.title
        holder.tvAmount.text = String.format("R %.2f", expense.amount)
        holder.tvCategory.text = expense.category
        holder.tvDate.text = formateDate(expense.date)

        holder.itemView.setOnClickListener {
            view ->
            val action = DashboardFragmentDirections.actionDashboardFragmentToUpdateExpenseFragment(expense)
            view.findNavController().navigate(action)
        }
    }
    override fun getItemCount(): Int = expenseList.size

    fun setExpenses(expenses: List<ExpenseEntity>) {
        this.expenseList = expenses
        notifyDataSetChanged()
    }
}








