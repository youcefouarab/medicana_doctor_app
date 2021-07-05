package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.medicana.*
import com.example.medicana.entity.Treatment
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.room.RoomService
import com.example.medicana.util.*
import com.example.medicana.viewmodel.VM.vm
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_appointment.*
import kotlinx.android.synthetic.main.layout_need_auth.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentFragment : Fragment() {

    private lateinit var act: Activity

    private var startDate: Long? = null
    private var finishDate: Long? = null

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
            inflater.inflate(R.layout.fragment_appointment, container, false)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.GONE
        appointment_toolbar?.setupWithNavController(navController(act))
        appointment_toolbar?.title = ""

        if (appointment_bottom_sheet != null) {
            BottomSheetBehavior.from(appointment_bottom_sheet).apply {
                peekHeight = 250
                this.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        val datePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        datePickerBuilder.setTitleText(R.string.pick_date_range)
        val datePicker = datePickerBuilder.build()
        datePicker.addOnPositiveButtonClickListener {
            try {
                startDate = it.first!! / 1000
                finishDate = it.second!! / 1000
                treatment_date_picker?.text = displayDateFromUnix(startDate!!) + " - " + displayDateFromUnix(finishDate!!)
            } catch (t: Throwable) {

            }
        }
        datePicker.addOnDismissListener {
            treatment_date_picker?.isClickable = true
        }

        val myAppointment = vm.myAppointment
        if (myAppointment != null) {
            appointment_patient_name?.text = myAppointment.first_name + " " + myAppointment.last_name
            appointment_patient_phone?.text = myAppointment.phone_number
            if (appointment_patient_photo != null) {
                Glide.with(act).load(R.drawable.default_profile).into(appointment_patient_photo)
            }
            try {
                appointment_date?.text = displayDate(myAppointment.date!!)
            } catch (t: Throwable) {

            }
            appointment_time?.text = myAppointment.start_time
            if (myAppointment.treatment_id != null) {
                toggleButtonsAndEdittexts(false)
                treatment_date_picker?.text = ""
                try {
                    treatment_date_picker?.text = displayDate(myAppointment.start_date!!) + " - " + displayDate(myAppointment.finish_date!!)
                } catch (t: Throwable) {

                }
                treatment_description?.setText(myAppointment.description)
            } else {
                treatment_date_picker?.setOnClickListener {
                    treatment_date_picker?.isClickable = false
                    datePicker.show(parentFragmentManager, "DATE_RANGE_PICKER")
                }
                prescribe_treatment?.setOnClickListener {
                    if (startDate != null && finishDate != null && !treatment_description?.text.isNullOrEmpty()) {
                        toggleButtonsAndEdittexts(false)
                        try {
                            val call = RetrofitService.endpoint.prescribeTreatment(
                                    jsonDateFromUnix(startDate!!),
                                    jsonDateFromUnix(finishDate!!),
                                    treatment_description.text.toString(),
                                    myAppointment.appointment_id,
                                    myAppointment.patient_id
                            )
                            call.enqueue(object : Callback<String> {
                                override fun onResponse(
                                        call: Call<String>?,
                                        response: Response<String>?
                                ) {
                                    if (response?.isSuccessful == true) {
                                        when {
                                            response.body() == RES_EXISTS -> {
                                                Toast.makeText(act, R.string.already_prescribed, Toast.LENGTH_LONG).show()
                                            }
                                            response.body() == RES_ERROR -> {
                                                toggleButtonsAndEdittexts(true)
                                                checkFailure(act, null)
                                            }
                                            else -> {
                                                Toast.makeText(act, R.string.prescribed_success, Toast.LENGTH_LONG).show()
                                                try {
                                                    RoomService.appDatabase.getTreatmentDao().addTreatment(
                                                        Treatment(
                                                            treatment_id = response.body()?.toLong(),
                                                            start_date = jsonDateFromUnix(startDate!!),
                                                            finish_date = jsonDateFromUnix(finishDate!!),
                                                            description = treatment_description?.text.toString()
                                                        )
                                                    )
                                                    RoomService.appDatabase.getAppointmentDao().setAppointmentTreatment(
                                                        response.body()?.toLong(),
                                                        myAppointment.appointment_id
                                                    )
                                                } catch (t: Throwable) {

                                                }
                                            }
                                        }
                                    } else {
                                        toggleButtonsAndEdittexts(true)
                                        checkFailure(act, null)
                                    }
                                }

                                override fun onFailure(call: Call<String>?, t: Throwable?) {
                                    toggleButtonsAndEdittexts(true)
                                    checkFailure(act, t)
                                }
                            })
                        } catch (t: Throwable) {

                        }
                    } else {
                        toggleButtonsAndEdittexts(true)
                        Toast.makeText(act, R.string.describe_treatment, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        need_auth_toolbar?.setupWithNavController(navController(act))
        need_auth_toolbar?.title = ""
        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_nav_host_to_authFragment)
        }
    }

    private fun toggleButtonsAndEdittexts(interact: Boolean) {
        if (interact) {
            prescribe_treatment?.isClickable = true
            treatment_date_picker?.isClickable = true
            treatment_description?.isEnabled = true
            prescribe_treatment?.setBackgroundResource(R.drawable.bg_rounded_small)
            prescribe_treatment?.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            prescribe_treatment?.isClickable = false
            treatment_date_picker?.isClickable = false
            treatment_description?.isEnabled = false
            prescribe_treatment?.setBackgroundResource(R.drawable.bg_rounded_gray_small)
            prescribe_treatment?.setTextColor(Color.parseColor("#8C8C8C"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vm.myAppointment = null
    }

}