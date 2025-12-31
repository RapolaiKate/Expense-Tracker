package com.example.expensetrackerclean.ui.updateexpense

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.expensetrackerclean.R
import com.example.expensetrackerclean.data.database.ExpenseEntity
import com.example.expensetrackerclean.data.viewmodel.DashboardViewModel
import com.example.expensetrackerclean.utils.formateDate
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date


class UpdateExpenseFragment : Fragment() {

    // ===== UI VIEWS =====
    private lateinit var editTitle: EditText
    private lateinit var editAmount: EditText
    private lateinit var editCategory: EditText
    private lateinit var editDate: EditText
    private lateinit var btnUpdateExpense: MaterialButton
    private lateinit var btnDeleteExpense: MaterialButton

    // ===== ERROR TEXT VIEWs =====
    private lateinit var tvErrorTitle: TextView
    private lateinit var tvErrorAmount: TextView
    private lateinit var tvErrorCategory: TextView
    private lateinit var tvErrorDate: TextView

    // ===== VIEWMODEL =====
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    // ===== SELECTED DATE =====
    private var selectedDate: Long = 0L

    // ===== CURRENT EXPENSE =====
    private var currentExpense: ExpenseEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_update_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ===== INITIALIZE VIEWS =====
        editTitle = view.findViewById(R.id.editTitle)
        editAmount = view.findViewById(R.id.editAmount)
        editCategory = view.findViewById(R.id.editCategory)
        editDate = view.findViewById(R.id.editDate)
        btnUpdateExpense = view.findViewById(R.id.btnUpdateExpense)
        btnDeleteExpense = view.findViewById(R.id.btnDeleteExpense)

        // ====== ERROR VIEWS =====
        tvErrorTitle = view.findViewById(R.id.tvErrorTitle)
        tvErrorAmount = view.findViewById(R.id.tvErrorAmount)
        tvErrorCategory = view.findViewById(R.id.tvErrorCategory)
        tvErrorDate = view.findViewById(R.id.tvErrorDate)

        // ===== GET EXPENSE FROM ARGUMENTS =====
        currentExpense = arguments?.getParcelable("expense")

        // ===== POPULAR FIELDS =====
        currentExpense?.let { expense ->
            editTitle.setText(expense.title)
            editAmount.setText(expense.amount.toString())
            editCategory.setText(expense.category)
            editDate.setText(formateDate(expense.date))
        }
        // === == DATE PICKER === ==
        editDate.setOnClickListener {
            showDatePicker()
        }
        // ===== UPDATE BUTTON =====
        btnUpdateExpense.setOnClickListener {
            updateExpense()
        }
        // ===== DELETE BUTTON =====
        btnDeleteExpense.setOnClickListener {
        currentExpense?.let {
            dashboardViewModel.deleteExpense(it)
            findNavController().popBackStack()
        }
    }
}
private fun updateExpense() {

    // ===== SAVE UPDATE EXPENSE WITH VALIDATION =====
    resetErrors()

    val title = editTitle.text.toString().trim()
    val amountText = editAmount.text.toString().trim()
    val category = editCategory.text.toString().trim()

    var isValid = true

    // ===== VALIDATION & ERROR HANDLING =====
    if (title.isEmpty()) {
        tvErrorTitle.text = "Title required"
        tvErrorTitle.visibility = View.VISIBLE
        isValid = false
    }
    if (amountText.isEmpty()) {
        tvErrorAmount.text = "Amount required"
        tvErrorAmount.visibility = View.VISIBLE
        isValid = false
    }
    if (category.isEmpty()) {
        tvErrorCategory.text = "Category required"
        tvErrorCategory.visibility = View.VISIBLE
        isValid = false
    }
    if (selectedDate <= 0L) {
        tvErrorDate.text = "Date required"
        tvErrorDate.visibility = View.VISIBLE
        isValid = false
    }
    if (!isValid) return

    val updateExpense = currentExpense!!.copy(
        title = title,
        amount = amountText.toDouble(),
        category = category,
        date = selectedDate
    )
    dashboardViewModel.updateExpense(updateExpense)
    findNavController().popBackStack()
}
    private fun resetErrors() {

        tvErrorTitle.visibility = View.GONE
        tvErrorAmount.visibility = View.GONE
        tvErrorCategory.visibility = View.GONE
        tvErrorDate.visibility = View.GONE
    }
    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(requireContext(), {_, year, month, day ->
            calendar.set(year, month, day)
            selectedDate = calendar.timeInMillis
            editDate.setText(formateDate(selectedDate))
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))
        dialog.show()
    }
    private fun formatDate(timestamp: Long): String {

        val sdf = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

