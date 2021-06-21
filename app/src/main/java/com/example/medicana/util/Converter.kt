package com.example.medicana.util

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date

class Converter {
    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let { SimpleDateFormat("yyyy-mm-dd").parse(it) }
    }
    @SuppressLint("SimpleDateFormat")
    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return SimpleDateFormat("yyyy-mm-dd").format(date!!)
    }
}