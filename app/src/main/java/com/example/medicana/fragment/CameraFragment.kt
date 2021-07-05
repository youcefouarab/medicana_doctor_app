package com.example.medicana.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.ui.setupWithNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.medicana.R
import com.example.medicana.entity.Appointment
import com.example.medicana.entity.MyAppointment
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.checkFailure
import com.example.medicana.util.navController
import com.example.medicana.viewmodel.VM.vm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_camera.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CameraFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        camera_toolbar?.setupWithNavController(navController(act))
        camera_toolbar?.title = ""

        scan()
    }

    private fun scan() {
        try {
            codeScanner = CodeScanner(act, the_scanner)
            codeScanner.apply {
                camera = CodeScanner.CAMERA_BACK
                formats = CodeScanner.ALL_FORMATS

                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.SINGLE
                isAutoFocusEnabled = true
                isFlashEnabled = false

                decodeCallback = DecodeCallback {
                    act.runOnUiThread {
                        try {
                            val id = it.text.toLong()
                            getAppointmentAndGo(id)
                        } catch (e: NumberFormatException) {
                            navController(act).navigateUp()
                            Toast.makeText(act, R.string.no_appointments, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                errorCallback = ErrorCallback {
                    act.runOnUiThread {
                        navController(act).navigateUp()
                        checkFailure(act, null)
                    }
                }
            }
            codeScanner.startPreview()
        } catch (t: Throwable) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        codeScanner.releaseResources()
    }

    private fun getAppointmentAndGo(appointmentId: Long) {
        try {
            val myAppointment = RoomService.appDatabase.getAppointmentDao().getMyAppointment(appointmentId)
            if (myAppointment.appointment_id == null) {
                val call = RetrofitService.endpoint.getMyAppointment(SharedPrefs(act).doctorId, appointmentId)
                call.enqueue(object : Callback<MyAppointment> {
                    override fun onResponse(
                            call: Call<MyAppointment>?,
                            response: Response<MyAppointment>?
                    ) {
                        if (response?.isSuccessful == true) {
                            if (response.body() != null) {
                                vm.myAppointment = response.body()
                                RoomService.appDatabase.getAppointmentDao().addMyAppointment(
                                    Appointment(
                                        appointment_id = vm.myAppointment?.appointment_id,
                                        patient_id = vm.myAppointment?.patient_id,
                                        date = vm.myAppointment?.date,
                                        start_time = vm.myAppointment?.start_time,
                                        finish_time = vm.myAppointment?.finish_time
                                    )
                                )
                                navController(act).navigate(R.id.action_cameraFragment_to_appointmentFragment)
                            } else {
                                navController(act).navigateUp()
                                Toast.makeText(act, R.string.no_appointments, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            checkFailure(act, null)
                        }
                    }

                    override fun onFailure(call: Call<MyAppointment>?, t: Throwable?) {
                        checkFailure(act, t)
                    }
                })
            } else {
                vm.myAppointment = myAppointment
                navController(act).navigate(R.id.action_cameraFragment_to_appointmentFragment)
            }
        } catch (t: Throwable) {
            navController(act).navigateUp()
            Toast.makeText(act, R.string.no_appointments, Toast.LENGTH_LONG).show()
        }
    }

}