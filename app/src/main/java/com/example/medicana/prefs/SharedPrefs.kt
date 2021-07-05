package com.example.medicana.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefs(context: Context) {

    companion object {
        private const val PREFS_FILENAME = "com.example.medicanafordoctors"
        private const val KEY_CONNECTED = "connected"
        private const val KEY_DOCTOR_ID = "doctor_id"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_ADDRESS = "address"
        private const val KEY_GENDER = "gender"
        private const val KEY_PHOTO = "photo"
        private const val KEY_SPECIALTY = "specialty"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_TOKEN = "token"
    }

    private val sharedPrefs: SharedPreferences =
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    var connected: Boolean
        get() = sharedPrefs.getBoolean(KEY_CONNECTED, false)
        set(value) = sharedPrefs.edit { putBoolean(KEY_CONNECTED, value) }

    var doctorId: Long
        get() = sharedPrefs.getLong(KEY_DOCTOR_ID, 0)
        set(value) = sharedPrefs.edit { putLong(KEY_DOCTOR_ID, value) }

    var phoneNumber: String?
        get() = sharedPrefs.getString(KEY_PHONE_NUMBER, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_PHONE_NUMBER, value) }

    var firstName: String?
        get() = sharedPrefs.getString(KEY_FIRST_NAME, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_FIRST_NAME, value) }

    var lastName: String?
        get() = sharedPrefs.getString(KEY_LAST_NAME, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_LAST_NAME, value) }

    var address: String?
        get() = sharedPrefs.getString(KEY_ADDRESS, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_ADDRESS, value) }

    var gender: String?
        get() = sharedPrefs.getString(KEY_GENDER, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_GENDER, value) }

    var photo: String?
        get() = sharedPrefs.getString(KEY_PHOTO, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_PHOTO, value) }

    var specialty: String?
        get() = sharedPrefs.getString(KEY_SPECIALTY, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_SPECIALTY, value) }

    var deviceId: Long
        get() = sharedPrefs.getLong(KEY_DEVICE_ID, 0)
        set(value) = sharedPrefs.edit { putLong(KEY_DEVICE_ID, value) }

    var token: String?
        get() = sharedPrefs.getString(KEY_TOKEN, "") ?: ""
        set(value) = sharedPrefs.edit { putString(KEY_TOKEN, value) }

}