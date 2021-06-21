package com.example.medicana.entity

import java.io.Serializable


data class MyAppointment(
    val appointment_id: Long?,
    val patient_id: Long?,
    val phone_number: String?,
    val first_name: String?,
    val last_name: String?,
    val date: String?,
    val time: String?

): Serializable
