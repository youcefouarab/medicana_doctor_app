package com.example.medicana.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.navigation.ui.setupWithNavController
import com.example.medicana.R
import com.example.medicana.SHARED_PREFS
import com.example.medicana.entity.Advice
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.Doctor
import com.example.medicana.entity.Patient
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        auth_toolbar?.setupWithNavController(navController(act))
        auth_toolbar?.title = ""

        auth_login_button?.setOnClickListener {
            if ((auth_phone.text.isNotEmpty()) && (auth_password.text.isNotEmpty())) {
                val phone = auth_phone.text.toString()
                auth_phone.text.clear()
                val password = auth_password.text.toString()
                auth_password.text.clear()
                val call = RetrofitService.endpoint.authDoctor(phone, password)
                call.enqueue(object : Callback<Doctor> {
                    override fun onResponse(
                        call: Call<Doctor>?,
                        response: Response<Doctor>?
                    ) {
                        if (response?.isSuccessful!!) {
                            val doctor = response.body()
                            if (doctor?.doctor_id != null) {
                                val pref =
                                    act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                                pref.edit {
                                    putBoolean("connected", true)
                                    putLong("doctor_id", doctor.doctor_id)
                                    putString("phone_number", doctor.phone_number)
                                    putString("first_name", doctor.first_name)
                                    putString("last_name", doctor.last_name)
                                    putString("address", doctor.address)
                                    putString("gender", doctor.gender)
                                    putString("photo", doctor.photo)
                                    putString("specialty", doctor.specialty)
                                }
                                reloadRoomDatabase(doctor.doctor_id)
                                Toast.makeText(act, R.string.welcome, Toast.LENGTH_LONG).show()
                                navController(act).navigateUp()
                            } else {
                                Toast.makeText(act, R.string.login_error, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            checkFailure(act)
                        }
                    }

                    override fun onFailure(call: Call<Doctor>?, t: Throwable?) {
                        Log.e("Retrofit error", t.toString())
                        checkFailure(act)
                    }
                })
            } else {
                Toast.makeText(act, "Please login with your phone number and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reloadRoomDatabase(doctor_id: Long) {
        val call1 = RetrofitService.endpoint.getMyAppointments(doctor_id)
        call1.enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(
                call: Call<List<Appointment>>?,
                response: Response<List<Appointment>>?
            ) {
                if (response?.isSuccessful!!) {
                    val appointments = response.body()
                    val call2 = RetrofitService.endpoint.getMyPatients(doctor_id)
                    call2.enqueue(object : Callback<List<Patient>> {
                        override fun onResponse(
                            call: Call<List<Patient>>?,
                            response: Response<List<Patient>>?
                        ) {
                            if (response?.isSuccessful!!) {
                                val patients = response.body()
                                val call3 = RetrofitService.endpoint.getAllAdvice(doctor_id)
                                call3.enqueue(object : Callback<List<Advice>> {
                                    override fun onResponse(
                                        call: Call<List<Advice>>?,
                                        response: Response<List<Advice>>?
                                    ) {
                                        if (response?.isSuccessful!!) {
                                            try {
                                                RoomService.appDatabase.getAppointmentDao()
                                                    .addMyAppointments(appointments!!)
                                                RoomService.appDatabase.getPatientDao()
                                                    .addMyPatients(patients!!)
                                                RoomService.appDatabase.getAdviceDao()
                                                    .addMyAdvice(response.body()!!)
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    act,
                                                    "Your information wasn't completely restored!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        } else {
                                            checkFailure(act)
                                        }
                                    }

                                    override fun onFailure(call: Call<List<Advice>>?, t: Throwable?) {
                                        Log.e("Retrofit error", t.toString())
                                        checkFailure(act)
                                    }
                                })


                            } else {
                                checkFailure(act)
                            }
                        }

                        override fun onFailure(call: Call<List<Patient>>?, t: Throwable?) {
                            Log.e("Retrofit error", t.toString())
                            checkFailure(act)
                        }
                    })

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