package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.MyAppointment

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointment NATURAL JOIN patient")
    fun getMyAppointments(): List<MyAppointment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointment(appointment: Appointment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointments(appointments: List<Appointment>)

    @Query("DELETE FROM appointment WHERE appointment_id = :appointment_id")
    fun deleteMyAppointment(appointment_id: Long?)

    @Query("SELECT count(*) FROM appointment WHERE doctor_id = :doctor_id")
    fun checkAppointmentsWithDoctorExist(doctor_id: Long?): Int

    @Query("DELETE FROM appointment")
    fun deleteAll()

}
