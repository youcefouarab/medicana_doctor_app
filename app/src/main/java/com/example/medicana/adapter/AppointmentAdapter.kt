package com.example.medicana.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medicana.R
import com.example.medicana.entity.MyAppointment
import com.example.medicana.util.displayDate
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm


class AppointmentAdapter(val context: Context, val data: List<MyAppointment>): RecyclerView.Adapter<AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_appointment, parent, false)
        )
    }

    override fun getItemCount() = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.appointmentsPatientName.text = data[position].first_name + " " + data[position].last_name
        holder.appointmentsDate.text = displayDate(data[position].date!!)
        holder.appointmentsTime.text = data[position].time.toString()

        holder.itemView.setOnClickListener{
            vm.myAppointment = data[position]
            navController(context as Activity).navigate(R.id.action_appointmentsFragment_to_appointmentFragment)
        }
    }
}

class AppointmentViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val appointmentsPatientName = view.findViewById(R.id.appointments_patient_name) as TextView
    val appointmentsDate = view.findViewById(R.id.appointments_date) as TextView
    val appointmentsTime = view.findViewById(R.id.appointments_time) as TextView
}