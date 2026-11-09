package com.kate.expensetrackerclean.data

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val type: String
)