package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.entity.Treatment

@Dao
interface TreatmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTreatment(treatment: Treatment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyTreatments(treatments: List<Treatment>)

    @Query("DELETE FROM treatment")
    fun deleteAll()

}