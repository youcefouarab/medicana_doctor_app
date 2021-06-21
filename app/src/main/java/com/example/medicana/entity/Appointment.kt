package com.example.medicana.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medicana.util.unixTimestamp
import java.io.Serializable

@Entity(tableName = "appointment")
data class Appointment (
    @PrimaryKey
    val appointment_id: Long? = null,
    val doctor_id: Long?,
    val patient_id: Long? = null,
    val date: String?,
    val time: String?,
    val done: Int? = 0

): Serializable