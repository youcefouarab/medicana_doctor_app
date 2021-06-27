package com.example.medicana.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicana.entity.MyAppointment
import com.example.medicana.entity.Patient

class MainViewModel : ViewModel() {
    var patient: Patient? = null
    var myAppointment: MyAppointment? = null
}