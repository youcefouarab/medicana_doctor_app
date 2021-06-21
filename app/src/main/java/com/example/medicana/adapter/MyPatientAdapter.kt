package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.util.navController
import com.example.medicana.MainViewModel
import com.example.medicana.room.RoomService
import com.example.medicana.entity.Patient
import com.example.medicana.service.AdviceUpdateSyncService


class MyPatientAdapter(val context: Context, val data: List<Patient>): RecyclerView.Adapter<MyPatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPatientViewHolder {
        return MyPatientViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_my_patient, parent, false)
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyPatientViewHolder, position: Int) {
        holder.patientName.text = data[position].first_name + " " + data[position].last_name

        //Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctors_photo)
        Glide.with(context).load(R.drawable.default_profile).into(holder.patientPhoto)

        val unread = RoomService.appDatabase.getAdviceDao().checkUnreadFromPatient(data[position].patient_id)
        if (unread > 0) {
            Glide.with(context).load(R.drawable.ic_advice).into(holder.unreadIndicator)
        }

        holder.itemView.setOnClickListener{
            (ViewModelProvider(context as ViewModelStoreOwner).get(MainViewModel::class.java)).patient = data[position]
            navController(context as Activity).navigate(R.id.action_advicesFragment_to_adviceFragment)
            if (unread > 0) {
                RoomService.appDatabase.getAdviceDao().updateSeenAdvice(data[position].patient_id)
                scheduleSync()
            }
        }
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder().
        setRequiredNetworkType(NetworkType.CONNECTED).build()
        val req= OneTimeWorkRequest.Builder(AdviceUpdateSyncService::class.java).
        setConstraints(constraints).addTag("doctor_advice_update_constraints").
        build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork("doctor_advice_update_work", ExistingWorkPolicy.REPLACE,req)

    }
}

class MyPatientViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val patientPhoto = view.findViewById(R.id.my_patient_photo) as ImageView
    val patientName = view.findViewById(R.id.my_patient_name) as TextView
    val unreadIndicator = view.findViewById(R.id.unread_indicator) as ImageView
}
