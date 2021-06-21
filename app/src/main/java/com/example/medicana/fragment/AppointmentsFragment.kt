package com.example.medicana.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.SHARED_PREFS
import com.example.medicana.adapter.AppointmentAdapter
import com.example.medicana.room.RoomService
import com.example.medicana.util.navController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_appointments.*
import kotlinx.android.synthetic.main.layout_need_auth.*

class AppointmentsFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val connected =
            (act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE))
            .getBoolean("connected", false)
        return if (connected) {
            inflater.inflate(R.layout.fragment_appointments, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE

        appointments_list?.layoutManager = LinearLayoutManager(act)
        appointments_list?.adapter = AppointmentAdapter(act, RoomService.appDatabase.getAppointmentDao().getMyAppointments())

        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_appointmentsFragment_to_authFragment)
        }

    }
}