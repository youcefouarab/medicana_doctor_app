package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.service.AdviceAddSyncService
import com.example.medicana.adapter.AdviceAdapter
import com.example.medicana.room.RoomService
import com.example.medicana.entity.Advice
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_advice.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdviceFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advice, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        act.nav_bottom?.visibility = View.GONE

        advice_toolbar?.setupWithNavController(navController(act))
        advice_toolbar?.title = ""

        val patient = vm.patient

        val doctorId = SharedPrefs(act).doctorId

        advice_patient_name?.text = patient?.first_name + " " + patient?.last_name

        //Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctors_photo)
        if (advice_patient_photo != null) {
            Glide.with(act).load(R.drawable.default_profile).into(advice_patient_photo!!)
        }

        advice_list?.layoutManager = LinearLayoutManager(act).apply {
            stackFromEnd = true
            reverseLayout = false
        }

        reload(false)
        Handler(Looper.getMainLooper()).postDelayed({
            reload(true)
        },10000)

        advice_send?.setOnClickListener {
            val message = advice_message_to_send?.text.toString()
            if (message.isNotBlank()) {
                advice_message_to_send?.text?.clear()
                RoomService.appDatabase.getAdviceDao().addAdvice(
                        Advice(
                                doctor_id = doctorId,
                                patient_id = patient?.patient_id,
                                reply = message,
                        )
                )
                scheduleSync()
                reload(false)
            }

        }

    }

    private fun reload(withState: Boolean) {
        val patient = vm.patient

        val recyclerViewState1 = advice_list?.layoutManager?.onSaveInstanceState()
        advice_list?.adapter = AdviceAdapter(act, RoomService.appDatabase.getAdviceDao().getAdviceWithPatient(patient?.patient_id))
        if (withState) advice_list?.layoutManager?.onRestoreInstanceState(recyclerViewState1)

        val call = RetrofitService.endpoint.getAdviceWithPatient(patient?.patient_id, SharedPrefs(act).doctorId)
        call.enqueue(object : Callback<List<Advice>> {
            override fun onResponse(
                call: Call<List<Advice>>?,
                response: Response<List<Advice>>?
            ) {
                if (response?.isSuccessful!!) {
                    RoomService.appDatabase.getAdviceDao().addMyAdvice(response.body()!!)
                    val recyclerViewState2 = advice_list?.layoutManager?.onSaveInstanceState()
                    val advice = RoomService.appDatabase.getAdviceDao().getAdviceWithPatient(patient?.patient_id)
                    val oldCount = advice_list?.adapter?.itemCount
                    advice_list?.adapter = AdviceAdapter(act, advice)
                    if (withState || advice.size <= oldCount!!) advice_list?.layoutManager?.onRestoreInstanceState(recyclerViewState2)
                } else {
                    checkFailure(act)
                }
            }

            override fun onFailure(call: Call<List<Advice>>?, t: Throwable?) {
                Log.e("Retrofit error", t.toString())
                checkFailure(act)
            }
        })

    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder().
        setRequiredNetworkType(NetworkType.CONNECTED).build()
        val req= OneTimeWorkRequest.Builder(AdviceAddSyncService::class.java).
        setConstraints(constraints).addTag("doctor_advice_add_constraints").build()
        val workManager = WorkManager.getInstance(act)
        workManager.enqueueUniqueWork("doctor_advice_add_work", ExistingWorkPolicy.REPLACE,req)

    }
}