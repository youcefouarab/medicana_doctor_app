package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.MyAppointment

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointment A LEFT JOIN treatment ON A.treatment_id = treatment.treatment_id NATURAL JOIN patient")
    fun getMyAppointments(): List<MyAppointment>

    @Query("SELECT * FROM appointment A LEFT JOIN treatment ON A.treatment_id = treatment.treatment_id NATURAL JOIN patient WHERE appointment_id = :appointment_id")
    fun getMyAppointment(appointment_id: Long?): MyAppointment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointment(appointment: Appointment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointments(appointments: List<Appointment>)

    @Query("UPDATE appointment SET treatment_id = :treatment_id WHERE appointment_id = :appointment_id")
    fun setAppointmentTreatment(treatment_id: Long, appointment_id: Long)

    @Query("DELETE FROM appointment")
    fun deleteAll()

}
