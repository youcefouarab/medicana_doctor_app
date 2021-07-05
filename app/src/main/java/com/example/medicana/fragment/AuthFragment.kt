package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupWithNavController
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.R
import com.example.medicana.entity.*
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        auth_toolbar?.setupWithNavController(navController(act))
        auth_toolbar?.title = ""

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            act.window.statusBarColor = ContextCompat.getColor(act, R.color.white)
        }

        auth_login_button?.setOnClickListener {
            if ((auth_phone?.text?.isNotEmpty() == true) && (auth_password?.text?.isNotEmpty() == true)) {
                val phone = auth_phone.text.toString()
                auth_phone.isEnabled = false
                val password = auth_password.text.toString()
                auth_password.isEnabled = false
                val call = RetrofitService.endpoint.authDoctor(phone, password)
                call.enqueue(object : Callback<Doctor> {
                    override fun onResponse(
                        call: Call<Doctor>?,
                        response: Response<Doctor>?
                    ) {
                        if (response?.isSuccessful == true) {
                            val doctor = response.body()
                            if (doctor?.doctor_id != null) {
                                val prefs = SharedPrefs(act)
                                prefs.connected = true
                                prefs.doctorId = doctor.doctor_id
                                prefs.phoneNumber = doctor.phone_number
                                prefs.firstName = doctor.first_name
                                prefs.lastName = doctor.last_name
                                prefs.address = doctor.address
                                prefs.gender = doctor.gender
                                prefs.photo = doctor.photo
                                prefs.specialty = doctor.specialty
                                reloadRoomDatabase(doctor.doctor_id)
                                registerToken(doctor.doctor_id)
                                subscribeToAppointments(doctor.doctor_id)
                                subscribeToAdvice(doctor.doctor_id)
                                Toast.makeText(act, R.string.welcome, Toast.LENGTH_LONG).show()
                                navController(act).navigateUp()
                            } else {
                                Toast.makeText(act, R.string.login_error, Toast.LENGTH_LONG).show()
                                auth_phone.isEnabled = true
                                auth_password.isEnabled = true
                            }
                        } else {
                            checkFailure(act, null)
                            auth_phone.isEnabled = true
                            auth_password.isEnabled = true
                        }
                    }

                    override fun onFailure(call: Call<Doctor>?, t: Throwable?) {
                        checkFailure(act, t)
                        auth_phone.isEnabled = true
                        auth_password.isEnabled = true
                    }
                })
            } else {
                Toast.makeText(act, R.string.missing_phone_pass, Toast.LENGTH_SHORT).show()
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
                if (response?.isSuccessful == true) {
                    val appointments = response.body()
                    val call2 = RetrofitService.endpoint.getMyPatients(doctor_id)
                    call2.enqueue(object : Callback<List<Patient>> {
                        override fun onResponse(
                            call: Call<List<Patient>>?,
                            response: Response<List<Patient>>?
                        ) {
                            if (response?.isSuccessful == true) {
                                val patients = response.body()
                                val call3 = RetrofitService.endpoint.getAllAdvice(doctor_id)
                                call3.enqueue(object : Callback<List<Advice>> {
                                    override fun onResponse(
                                        call: Call<List<Advice>>?,
                                        response: Response<List<Advice>>?
                                    ) {
                                        if (response?.isSuccessful == true) {
                                            val advice = response.body()
                                            val call4 = RetrofitService.endpoint.getMyTreatments(doctor_id)
                                            call4.enqueue(object : Callback<List<Treatment>> {
                                                override fun onResponse(
                                                        call: Call<List<Treatment>>?,
                                                        response: Response<List<Treatment>>?
                                                ) {
                                                    if (response?.isSuccessful == true) {
                                                        try {
                                                            RoomService.appDatabase.getAppointmentDao().addMyAppointments(appointments)
                                                            RoomService.appDatabase.getPatientDao().addMyPatients(patients)
                                                            RoomService.appDatabase.getAdviceDao().addMyAdvice(advice)
                                                            RoomService.appDatabase.getTreatmentDao().addMyTreatments(response.body())
                                                        } catch (t: Throwable) {
                                                            Toast.makeText(
                                                                    act,
                                                                    R.string.db_error,
                                                                    Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    } else {
                                                        checkFailure(act, null)
                                                    }
                                                }

                                                override fun onFailure(call: Call<List<Treatment>>?, t: Throwable?) {
                                                    checkFailure(act, t)
                                                }
                                            })
                                        } else {
                                            checkFailure(act, null)
                                        }
                                    }

                                    override fun onFailure(call: Call<List<Advice>>?, t: Throwable?) {
                                        checkFailure(act, t)
                                    }
                                })
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

    private fun registerToken(doctor_id: Long) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            } else {
                val call = RetrofitService.endpoint.registerToken(
                    user_id = doctor_id,
                    token = task.result
                )
                call.enqueue(object : Callback<Long> {
                    override fun onResponse(
                        call: Call<Long>?,
                        response: Response<Long>?
                    ) {
                        if (response?.isSuccessful!!) {
                            val prefs = SharedPrefs(act)
                            prefs.deviceId = response.body()!!
                            prefs.token = task.result
                        } else {
                            checkFailure(act, null)
                        }
                    }

                    override fun onFailure(call: Call<Long>?, t: Throwable?) {
                        checkFailure(act, t)
                    }
                })
            }
        })

    }

    private fun subscribeToAppointments(doctor_id: Long) {
        FirebaseMessaging.getInstance().subscribeToTopic("appointments-for-$doctor_id")
            .addOnCompleteListener { task ->
                var msg = "subscribed"
                if (!task.isSuccessful) {
                    msg = "not subscribed"
                }
                Log.d("firebase", msg)
            }
    }

    private fun subscribeToAdvice(doctor_id: Long) {
        FirebaseMessaging.getInstance().subscribeToTopic("ask-advice-from-$doctor_id")
            .addOnCompleteListener { task ->
                var msg = "subscribed"
                if (!task.isSuccessful) {
                    msg = "not subscribed"
                }
                Log.d("firebase", msg)
            }
    }

}