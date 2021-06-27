package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.medicana.*
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.util.displayDate
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_appointment.*
import kotlinx.android.synthetic.main.layout_need_auth.*

class AppointmentFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View
        val connected = SharedPrefs(act).connected
        view = if (connected) {
            inflater.inflate(R.layout.fragment_appointment, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        appointment_toolbar?.setupWithNavController(navController(act))
        appointment_toolbar?.title = ""

        BottomSheetBehavior.from(appointment_bottom_sheet!!).apply {
            peekHeight = 250
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val myAppointment = vm.myAppointment
        if (myAppointment != null) {
            appointment_patient_name?.text = myAppointment.first_name + " " + myAppointment.last_name
            appointment_patient_phone?.text = myAppointment.phone_number

            //Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctors_photo)
            if (appointment_patient_photo != null) {
                Glide.with(act).load(R.drawable.default_profile).into(appointment_patient_photo!!)
            }

            appointment_date?.text = displayDate(myAppointment.date!!)
            appointment_time?.text = myAppointment.time
        }

        need_auth_toolbar?.setupWithNavController(navController(act))
        need_auth_toolbar?.title = ""
        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_nav_host_to_authFragment)
        }
    }

}