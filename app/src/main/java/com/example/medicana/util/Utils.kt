package com.example.medicana.util

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medicana.R
import java.text.DateFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DEPRECATION")
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun navController(activity: Activity): NavController {
    val navHostFragment = (activity as AppCompatActivity)
            .supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
    return navHostFragment.navController
}

fun checkFailure(context: Context, t: Throwable?) {
    if (isOnline(context)) {
        Toast.makeText(
                context,
                R.string.error,
                Toast.LENGTH_SHORT
        ).show()
    } else {
        Toast.makeText(
                context,
                R.string.internet_error,
                Toast.LENGTH_SHORT
        ).show()
    }
    if (t != null) {
        Log.e("Retrofit error", t.toString())
    }
}

fun displayDate(date: String): String {
    val day = date.slice(IntRange(8, 9))
    val month = DateFormatSymbols().months[date.slice(IntRange(5, 6)).toInt() - 1]
    val year = date.slice(IntRange(0, 3))
    val lang = Locale.getDefault().language
    return if (LocalDate.now().year.toString() == year) {
        if (lang == "en") {
            "$month $day"
        } else {
            "$day $month"
        }
    } else {
        if (lang == "en") {
            "$month $day, $year"
        } else {
            "$day $month $year"
        }
    }

}

fun displayDateFromUnix(date_time: Long): String {
    val lang = Locale.getDefault().language
    val year = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date_time * 1000),
            ZoneId.systemDefault()
    ).format(DateTimeFormatter.ofPattern("yyyy"))
    val pattern = if (LocalDate.now().year.toString() == year) {
        if (lang == "en") {
            "MMM d"
        } else {
            "d MMM"
        }
    } else {
        if (lang == "en") {
            "MMM d, yyyy"
        } else {
            "d MMM yyyy"
        }
    }
    return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date_time * 1000),
            ZoneId.systemDefault()
    ).format(DateTimeFormatter.ofPattern(pattern))

}

fun displayTimeFromUnix(date_time: Long): String {
    return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date_time * 1000),
            ZoneId.systemDefault()
    ).format(DateTimeFormatter.ofPattern("HH:mm"))
}


fun unixTimestamp(): Long {
    return System.currentTimeMillis() / 1000L
}

fun jsonDateFromUnix(date_time: Long): String {
    return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date_time * 1000),
            ZoneId.systemDefault()
    ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}