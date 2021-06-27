package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Patient

@Dao
interface PatientDao {
    @Query("SELECT * FROM patient")
    fun getMyPatients(): List<Patient>

    @Query("SELECT * FROM patient WHERE patient_id = :patient_id")
    fun getMyPatient(patient_id: Long): Patient

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyPatient(patient: Patient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyPatients(patients: List<Patient>)

    @Query("DELETE FROM patient WHERE patient_id = :patient_id")
    fun deleteMyPatient(patient_id: Long?)

    @Query("SELECT count(*) FROM patient WHERE patient_id = :patient_id")
    fun checkPatientExists(patient_id: Long?): Int

    @Query("DELETE FROM patient")
    fun deleteAll()

}