package com.example.medicana.dao

import androidx.room.*
import com.example.medicana.util.MESSAGE_SEEN
import com.example.medicana.util.MESSAGE_SENT
import com.example.medicana.entity.Advice

@Dao
interface AdviceDao {
    @Query("SELECT * FROM advice WHERE patient_id = :patient_id")
    fun getAdviceWithPatient(patient_id: Long?): List<Advice>

    @Query("SELECT * FROM advice WHERE is_sync = 0 AND reply IS NOT NULL")
    fun getAdviceToSyncAdd(): List<Advice>

    @Query("SELECT patient_id FROM advice WHERE is_sync = 0 AND message IS NOT NULL")
    fun getAdvicePatientsToSyncUpdate(): List<Long>

    @Update
    fun updateSyncedAdvice(advice_list: List<Advice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAdvice(advice: Advice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addMyAdvice(advice: List<Advice>)

    @Query("DELETE FROM advice WHERE advice_id = :advice_id")
    fun deleteAdvice(advice_id: Long?)

    @Query("SELECT count(*) FROM advice WHERE patient_id = :patient_id AND message IS NOT NULL AND state = '$MESSAGE_SENT' ")
    fun checkUnreadFromPatient(patient_id: Long?): Int

    @Query("UPDATE advice SET state = '$MESSAGE_SEEN', is_sync = 0 WHERE patient_id = :patient_id AND message IS NOT NULL")
    fun updateSeenAdvice(patient_id: Long?)

    @Query("UPDATE advice SET is_sync = 1 WHERE patient_id = :patient_id AND message IS NOT NULL")
    fun updateSyncedSeenAdvice(patient_id: Long?)

    @Query("DELETE FROM advice")
    fun deleteAll()

}
