package com.example.medicana.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "appointment")
data class Appointment (
    @PrimaryKey
    val appointment_id: Long? = null,
    val doctor_id: Long? = null,
    val patient_id: Long? = null,
    val date: String?,
    val time: String?,
    val treatment_id: Long? = null

): Serializable