package com.example.medicana

import android.app.Application
import com.example.medicana.room.RoomService

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        RoomService.context = applicationContext
    }

}