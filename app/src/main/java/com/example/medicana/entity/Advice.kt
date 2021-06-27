package com.example.medicana.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medicana.util.MESSAGE_SENT
import com.example.medicana.util.unixTimestamp
import java.io.Serializable

@Entity(tableName = "advice")
data class Advice (
        @PrimaryKey
        val advice_id: Long? = null,
        val doctor_id: Long?,
        val patient_id: Long?,
        val message: String? = null,
        val reply: String? = null,
        val date_time: Long? = unixTimestamp(),
        val state: String? = MESSAGE_SENT,
        var is_sync: Int? = 0

): Serializable