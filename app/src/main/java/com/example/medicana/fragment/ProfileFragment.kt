package com.example.medicana.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.example.medicana.R
import com.example.medicana.SHARED_PREFS
import com.example.medicana.util.navController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.dimmed_bg
import kotlinx.android.synthetic.main.fragment_profile.profile_name
import kotlinx.android.synthetic.main.fragment_profile.profile_phone
import kotlinx.android.synthetic.main.layout_need_auth.*

class ProfileFragment : Fragment() {

    private lateinit var act: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val connected =
            (act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE))
                .getBoolean("connected", false)
        return if (connected) {
            inflater.inflate(R.layout.fragment_profile, container, false)
        } else {
            inflater.inflate(R.layout.layout_need_auth, container, false)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act.nav_bottom?.visibility = View.VISIBLE

        val pref = act.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        profile_name?.text = pref.getString("first_name", "") + " " + pref.getString("last_name", "")
        profile_phone?.text = pref.getString("phone_number", "")

        //Glide.with(context).load(BASE_URL + data[position].photo).into(holder.doctors_photo)
        if (profile_photo != null) {
            Glide.with(act).load(R.drawable.default_profile).into(profile_photo!!)
        }

        if (profile_bottom_sheet != null) {
            val bsBehave = BottomSheetBehavior.from(profile_bottom_sheet!!)
            bsBehave.apply {
                peekHeight = 250
                this.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            @Suppress("DEPRECATION")
            bsBehave.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) dimmed_bg.visibility = View.GONE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    dimmed_bg.visibility = View.VISIBLE
                    dimmed_bg.alpha = slideOffset
                }
            })
        }

        profile_logout?.setOnClickListener {
            pref.edit {
                putBoolean("connected", false)
            }
            navController(act).navigate(R.id.action_profileFragment_to_nav_host)
        }

        need_auth_button?.setOnClickListener {
            navController(requireActivity()).navigate(R.id.action_profileFragment_to_authFragment)
        }

    }
}