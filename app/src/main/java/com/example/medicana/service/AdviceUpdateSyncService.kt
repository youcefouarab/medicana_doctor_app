package com.example.medicana.service


import android.annotation.SuppressLint
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import com.example.medicana.RES_SUCCESS
import com.example.medicana.SHARED_PREFS
import com.example.medicana.room.RoomService
import com.example.medicana.retrofit.RetrofitService
import com.google.common.util.concurrent.ListenableFuture
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("RestrictedApi")
class AdviceUpdateSyncService(private val ctx: Context, private val workParameters: WorkerParameters): ListenableWorker(ctx, workParameters) {

    lateinit var  future: SettableFuture<Result>

    override fun startWork(): ListenableFuture<Result> {
        future = SettableFuture.create()
        val patients = RoomService.appDatabase.getAdviceDao().getAdvicePatientsToSyncUpdate()
        syncAdvice(patients)
        return future
    }

    private fun syncAdvice(patients: List<Long>) {
        val doctorId = (ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE))
                .getLong("doctor_id", 0)
        for (patient_id in patients) {
            val result = RetrofitService.endpoint.seeMessage(doctorId, patient_id)
            result.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    future.set(Result.retry())
                }
                override fun onResponse(call: Call<String>?, response: Response<String>?) {
                    if (response?.isSuccessful!!) {
                        if (response?.body() == RES_SUCCESS) {
                            RoomService.appDatabase.getAdviceDao().updateSyncedSeenAdvice(patient_id)
                            future.set(Result.success())
                        } else {
                            future.set(Result.retry())
                        }
                    } else {
                        future.set(Result.retry())
                    }
                }
            })
        }
    }


}