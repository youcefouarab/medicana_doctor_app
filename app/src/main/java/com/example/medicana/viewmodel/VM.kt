package com.example.medicana.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

@SuppressLint("StaticFieldLeak")
object VM {
    lateinit var context: Context
    val vm: MainViewModel by lazy {
        ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java)
    }
}