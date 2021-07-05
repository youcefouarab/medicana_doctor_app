package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.adapter.AppointmentAdapter
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.Patient
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import kotlinx.android.synthetic.main.fragment_old_appointments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class OldAppointmentsFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_old_appointments, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onResume() {
        super.onResume()

        old_appointments_list?.layoutManager = LinearLayoutManager(act)
        val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val time = SimpleDateFormat("HH:mm").format(Date())
        old_appointments_list?.adapter = AppointmentAdapter(act, RoomService.appDatabase.getAppointmentDao().getMyOldAppointments(date + "T00:00:00.000Z", time))
        val call1 = RetrofitService.endpoint.getMyAppointments(SharedPrefs(act).doctorId)
        call1.enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(
                call: Call<List<Appointment>>?,
                response: Response<List<Appointment>>?
            ) {
                if (response?.isSuccessful == true) {
                    val appointments = response.body()
                    val call2 = RetrofitService.endpoint.getMyPatients(SharedPrefs(act).doctorId)
                    call2.enqueue(object : Callback<List<Patient>> {
                        override fun onResponse(
                                call: Call<List<Patient>>?,
                                response: Response<List<Patient>>?
                        ) {
                            if (response?.isSuccessful == true) {
                                RoomService.appDatabase.getAppointmentDao().deleteAll()
                                RoomService.appDatabase.getAppointmentDao().addMyAppointments(appointments)
                                RoomService.appDatabase.getPatientDao().deleteAll()
                                RoomService.appDatabase.getPatientDao().addMyPatients(response.body())
                                old_appointments_list?.adapter = AppointmentAdapter(
                                        act,
                                        RoomService.appDatabase.getAppointmentDao().getMyOldAppointments(
                                                date + "T00:00:00.000Z",
                                                time
                                        )
                                )
                            } else {
                                checkFailure(act, null)
                            }
                        }

                        override fun onFailure(call: Call<List<Patient>>?, t: Throwable?) {
                            checkFailure(act, t)
                        }
                    })
                } else {
                    checkFailure(act, null)
                }
            }

            override fun onFailure(call: Call<List<Appointment>>?, t: Throwable?) {
                checkFailure(act, t)
            }
        })

    }
}