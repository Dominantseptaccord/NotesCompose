package com.example.notes.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.notes.R
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object DataFormatter {
    private val timeMillisAsHour = TimeUnit.HOURS.toMillis(1)
    private val timeMillisAsDay = TimeUnit.DAYS.toMillis(1)
    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun formatCurrentDate() : String{
        return formatter.format(System.currentTimeMillis())
    }
    @Composable
    fun formatDateToString(timestamp: Long) : String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when{
            diff < timeMillisAsHour -> stringResource(R.string.just_now)
            diff < timeMillisAsDay -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                stringResource(R.string.h_ago, hours)
            }
            else -> {
                formatter.format(timestamp)
            }
        }
    }
}