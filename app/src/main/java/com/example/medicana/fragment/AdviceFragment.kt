package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
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
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_advice.*

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        reload()

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
                reload()
            }

        }

    }

    private fun reload() {
        val patient = vm.patient

        advice_list?.adapter = AdviceAdapter(act, RoomService.appDatabase.getAdviceDao().getAdviceWithPatient(patient?.patient_id))
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