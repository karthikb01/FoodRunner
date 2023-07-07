package com.example.foodrunner.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodrunner.R

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvMobileNumber: TextView
    private lateinit var tvEmailAddress: TextView
    private lateinit var tvDeliverAddress: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.tvNameProfile)
        tvMobileNumber = view.findViewById(R.id.tvMobileNumberProfile)
        tvEmailAddress = view.findViewById(R.id.tvEmailAddressProfile)
        tvDeliverAddress = view.findViewById(R.id.tvDeliverAddressProfile)

        val sharedPreferences = activity?.getSharedPreferences(
            "Food Runner Preferences",
            AppCompatActivity.MODE_PRIVATE
        )
        tvName.text = sharedPreferences?.getString("name", null)
        tvMobileNumber.text = sharedPreferences?.getString("mobile_number", null)
        tvEmailAddress.text = sharedPreferences?.getString("email", null)
        tvDeliverAddress.text = sharedPreferences?.getString("address", null)

        return view
    }


}