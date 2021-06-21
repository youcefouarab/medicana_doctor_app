package com.example.medicana.retrofit

import com.example.medicana.entity.*
import retrofit2.Call
import retrofit2.http.*

interface Endpoint {
    @GET("doctor_appointments/{doctor_id}")
    fun getMyAppointments(
        @Path("doctor_id") doctor_id: Long?
    ): Call<List<Appointment>>

    @GET("doctor_patients/{doctor_id}")
    fun getMyPatients(
        @Path("doctor_id") doctor_id: Long?
    ): Call<List<Patient>>

    @GET("auth_doctor/{phone_number}/{password}")
    fun authDoctor(
        @Path("phone_number") phone_number: String?,
        @Path("password") password: String?
    ): Call<Doctor>

    @POST("give_advice")
    fun giveAdvice(@Body advice_list: List<Advice>?): Call<String>

    @PUT("see_message/{doctor_id}/{patient_id}")
    fun seeMessage(
        @Path("doctor_id") doctor_id: Long?,
        @Path("patient_id") patient_id: Long?
    ): Call<String>

    @GET("all_doctor_advice/{doctor_id}")
    fun getAllAdvice(
            @Path("doctor_id") doctor_id: Long?
    ): Call<List<Advice>>
}

