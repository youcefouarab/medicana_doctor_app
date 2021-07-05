package com.example.medicana.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "treatment")
data class Treatment(
    @PrimaryKey
    val treatment_id: Long? = null,
    val start_date: String?,
    val finish_date: String?,
    val description: String?
): Serializable
