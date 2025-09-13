package com.example.civicsnap.models

data class ReportData(
    val title: String,
    val category: String,
    val priority: String,
    val description: String,
    val location: String,
    val photo_url: String,
    val timestamp: Long,
    val userId: String? = null
)
