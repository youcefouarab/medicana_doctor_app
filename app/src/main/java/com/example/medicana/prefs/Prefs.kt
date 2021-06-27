package com.example.medicana.prefs

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object Prefs {
    lateinit var context: Context
    /*val prefs: Prefs by lazy {
        SharedPrefs(context)
    }*/
}