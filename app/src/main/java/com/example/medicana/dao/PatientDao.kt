package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Patient

@Dao
interface PatientDao {

    @Query("SELECT * FROM patient")
    fun getMyPatients(): List<Patient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyPatients(patients: List<Patient?>?)

    @Query("DELETE FROM patient")
    fun deleteAll()

}