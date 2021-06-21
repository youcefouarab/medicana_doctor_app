package com.example.medicana.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "doctor")
data class Doctor (
    @PrimaryKey
    val doctor_id: Long? = null,
    val phone_number: String?,
    val first_name: String?,
    val last_name: String?,
    val address: String?,
    val gender: String?,
    val photo: String?,
    val specialty: String?,
    val longitude: Double?,
    val latitude: Double?

): Serializable