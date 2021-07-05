package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.MyAppointment

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM appointment LEFT JOIN treatment ON appointment.treatment_id = treatment.treatment_id LEFT JOIN patient ON appointment.patient_id = patient.patient_id WHERE date > :date OR (date = :date AND finish_time >= :time)")
    fun getMyCurrentAppointments(date: String?, time: String?): List<MyAppointment>

    @Query("SELECT * FROM appointment LEFT JOIN treatment ON appointment.treatment_id = treatment.treatment_id LEFT JOIN patient ON appointment.patient_id = patient.patient_id WHERE date < :date OR (date = :date AND finish_time < :time)")
    fun getMyOldAppointments(date: String?, time: String?): List<MyAppointment>

    @Query("SELECT * FROM appointment LEFT JOIN treatment ON appointment.treatment_id = treatment.treatment_id LEFT JOIN patient ON appointment.patient_id = patient.patient_id WHERE appointment_id = :appointment_id")
    fun getMyAppointment(appointment_id: Long?): MyAppointment

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointment(appointment: Appointment?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAppointments(appointments: List<Appointment?>?)

    @Query("UPDATE appointment SET treatment_id = :treatment_id WHERE appointment_id = :appointment_id")
    fun setAppointmentTreatment(treatment_id: Long?, appointment_id: Long?)

    @Query("DELETE FROM appointment")
    fun deleteAll()

}
