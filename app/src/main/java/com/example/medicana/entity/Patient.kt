package com.example.medicana.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "patient")
data class Patient(
    @PrimaryKey
    val patient_id: Long? = null,
    val phone_number: String?,
    val first_name: String?,
    val last_name: String?,
    val gender: String?,
    val photo: String?

): Serializable
