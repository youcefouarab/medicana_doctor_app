package com.example.medicana.entity

import java.io.Serializable


data class MyAppointment(
    //patient info
    val patient_id: Long?,
    val phone_number: String?,
    val first_name: String?,
    val last_name: String?,
    //appointment info
    val appointment_id: Long?,
    val date: String?,
    val start_time: String?,
    val finish_time: String?,
    //treatment info
    val treatment_id: Long?,
    val start_date: String?,
    val finish_date: String?,
    val description: String?

): Serializable
