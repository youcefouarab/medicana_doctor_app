package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicana.R
import com.example.medicana.adapter.AppointmentAdapter
import com.example.medicana.entity.Appointment
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import kotlinx.android.synthetic.main.fragment_current_appointments.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CurrentAppointmentsFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current_appointments, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onResume() {
        super.onResume()

        current_appointments_list?.layoutManager = LinearLayoutManager(act)

        val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val time = SimpleDateFormat("HH:mm").format(Date())

        current_appointments_list?.adapter = AppointmentAdapter(act, RoomService.appDatabase.getAppointmentDao().getMyCurrentAppointments(date + "T00:00:00.000Z", time))

        val call = RetrofitService.endpoint.getMyAppointments(SharedPrefs(act).doctorId)
        call.enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(
                call: Call<List<Appointment>>?,
                response: Response<List<Appointment>>?
            ) {
                if (response?.isSuccessful!!) {
                    RoomService.appDatabase.getAppointmentDao().deleteAll()
                    RoomService.appDatabase.getAppointmentDao().addMyAppointments(response.body()!!)
                    current_appointments_list?.adapter = AppointmentAdapter(
                        act,
                        RoomService.appDatabase.getAppointmentDao().getMyCurrentAppointments(
                            date + "T00:00:00.000Z",
                            time
                        )
                    )
                } else {
                    checkFailure(act)
                }
            }

            override fun onFailure(call: Call<List<Appointment>>?, t: Throwable?) {
                Log.e("Retrofit error", t.toString())
                checkFailure(act)
            }
        })
    }


}