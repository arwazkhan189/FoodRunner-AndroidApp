package me.arwazkhan.foodrunner.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import me.arwazkhan.foodrunner.R

class MyProfileFragment : Fragment() {
    private lateinit var txtName: TextView
    private lateinit var txtMobileNumber: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtAddress: TextView
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        txtName = view.findViewById(R.id.txtName)
        txtMobileNumber = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)

        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        txtName.text = sharedPreferences.getString("name", "Name")
        txtMobileNumber.text =
            "+91-${sharedPreferences.getString("mobile_number", "Mobile Number")}"
        txtEmail.text = sharedPreferences.getString("email", "Email")
        txtAddress.text = sharedPreferences.getString("address", "Delivery Address")

        return view

    }
}