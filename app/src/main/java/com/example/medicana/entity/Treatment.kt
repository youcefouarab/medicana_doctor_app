package com.example.medicana.entity

import java.io.Serializable

data class Treatment(
    val treatment_id: Long? = null,
    val description: String?,
    val start_date: String?,
    val finish_date: String?,
    val patient_id: Long?

): Serializable
