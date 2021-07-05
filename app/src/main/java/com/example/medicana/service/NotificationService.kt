package com.example.medicana.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.medicana.MainActivity
import com.example.medicana.R
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.retrofit.RetrofitService
import com.example.medicana.util.NOTIFICATION
import com.example.medicana.viewmodel.VM.context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random.Default.nextInt


class NotificationService: FirebaseMessagingService() {

    private val tag = "firebase"
    private val channelId = "medicanafordoctors_channel"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag, "new token : $token")

        //reRegisterToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(NOTIFICATION, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this, 100, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["body"])
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(channelId, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun reRegisterToken(token: String) {
        val prefs = SharedPrefs(context)
        if (prefs.connected) {
            val call1 = RetrofitService.endpoint.unregisterToken(
                prefs.deviceId
            )
            call1.enqueue(object : Callback<String> {
                override fun onResponse(
                    call: Call<String>?,
                    response: Response<String>?
                ) {
                    val call2 = RetrofitService.endpoint.registerToken(
                        user_id = prefs.doctorId,
                        token = token
                    )
                    call2.enqueue(object : Callback<Long> {
                        override fun onResponse(
                            call: Call<Long>?,
                            response: Response<Long>?
                        ) {
                            if (response?.isSuccessful!!) {
                                prefs.deviceId = response.body()!!
                                prefs.token = token
                            }
                        }

                        override fun onFailure(call: Call<Long>?, t: Throwable?) {
                            Log.e("Retrofit error", t.toString())
                        }
                    })
                }

                override fun onFailure(call: Call<String>?, t: Throwable?) {
                    Log.e("Retrofit error", t.toString())
                    val call2 = RetrofitService.endpoint.registerToken(
                        user_id = prefs.doctorId,
                        token = token
                    )
                    call2.enqueue(object : Callback<Long> {
                        override fun onResponse(
                            call: Call<Long>?,
                            response: Response<Long>?
                        ) {
                            if (response?.isSuccessful!!) {
                                prefs.deviceId = response.body()!!
                                prefs.token = token
                            }
                        }

                        override fun onFailure(call: Call<Long>?, t: Throwable?) {
                            Log.e("Retrofit error", t.toString())
                        }
                    })
                }
            })
        }
    }
}