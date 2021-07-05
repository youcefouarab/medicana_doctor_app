package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.medicana.R
import com.example.medicana.adapter.MyPatientAdapter
import com.example.medicana.entity.Advice
import com.example.medicana.entity.Patient
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_advices.*
import kotlinx.android.synthetic.main.layout_need_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdvicesFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val connected = SharedPrefs(act).connected
        return if (!connected) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                act.window.statusBarColor = ContextCompat.getColor(act, R.color.white)
            }
            inflater.inflate(R.layout.layout_need_auth, container, false)
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                act.window.statusBarColor = ContextCompat.getColor(act, R.color.medicana)
            }
            inflater.inflate(R.layout.fragment_advices, container, false)
        }
    }

    override fun onResume() {
        super.onResume()
        act.nav_bottom?.visibility = View.VISIBLE

        advices_patients_list?.layoutManager = LinearLayoutManager(act)
        advices_patients_list?.adapter = MyPatientAdapter(act, RoomService.appDatabase.getPatientDao().getMyPatients())

        val doctorId = SharedPrefs(act).doctorId

        val call1 = RetrofitService.endpoint.getMyPatients(doctorId)
        call1.enqueue(object : Callback<List<Patient>> {
            override fun onResponse(
                call: Call<List<Patient>>?,
                response: Response<List<Patient>>?
            ) {
                if (response?.isSuccessful!!) {
                    val patients = response.body()
                    val call2 = RetrofitService.endpoint.getAllAdvice(doctorId)
                    call2.enqueue(object : Callback<List<Advice>> {
                        override fun onResponse(
                            call: Call<List<Advice>>?,
                            response: Response<List<Advice>>?
                        ) {
                            if (response?.isSuccessful!!) {
                                RoomService.appDatabase.getPatientDao().deleteAll()
                                RoomService.appDatabase.getAdviceDao().deleteAll()
                                RoomService.appDatabase.getPatientDao().addMyPatients(patients!!)
                                RoomService.appDatabase.getAdviceDao().addMyAdvice(response.body()!!)
                                advices_patients_list?.adapter = MyPatientAdapter(act, RoomService.appDatabase.getPatientDao().getMyPatients())
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

        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_advicesFragment_to_authFragment)
        }

    }

}