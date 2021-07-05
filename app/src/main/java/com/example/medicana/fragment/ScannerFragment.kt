package com.example.medicana.fragment

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.medicana.R
import com.example.medicana.prefs.SharedPrefs
import com.example.medicana.util.navController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.layout_need_auth.*

class ScannerFragment : Fragment() {

    private lateinit var act: Activity
    private val cameraRequestCode = 101

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
            inflater.inflate(R.layout.fragment_scanner, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE

        scanner_btn?.setOnClickListener {
            setupPermissions()
        }

        need_auth_button?.setOnClickListener {
            navController(act).navigate(R.id.action_nav_host_to_authFragment)
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(act, android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            navController(act).navigate(R.id.action_nav_host_to_cameraFragment)
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(act, arrayOf(android.Manifest.permission.CAMERA), cameraRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            cameraRequestCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(act, R.string.need_camera_permission, Toast.LENGTH_SHORT).show()
                } else {
                    navController(act).navigate(R.id.action_nav_host_to_cameraFragment)
                }
            }
        }
    }

}