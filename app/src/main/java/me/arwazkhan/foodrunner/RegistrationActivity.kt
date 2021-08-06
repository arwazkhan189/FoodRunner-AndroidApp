package me.arwazkhan.foodrunner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast


class RegistrationActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var et_delivery_address: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        title = "Register Yourself"

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        et_delivery_address = findViewById(R.id.et_delivery_address)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val Name = etName.text.toString()
            val Email = etEmail.text.toString()
            val MobileNumber = etMobileNumber.text.toString()
            val DeliveryAddress = et_delivery_address.text.toString()
            val Password = etPassword.text.toString()
            val ConfirmPassword = etConfirmPassword.text.toString()

            val intent = Intent(this@RegistrationActivity, RegisterDetailsActivity::class.java)

            if (Password == ConfirmPassword && Name.isNotEmpty() && Email.isNotEmpty() && MobileNumber.isNotEmpty() && DeliveryAddress.isNotEmpty() && Password.isNotEmpty()) {
                savePreferences(Name, Email, MobileNumber, DeliveryAddress)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Password not matched or Details are not filled",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun savePreferences(
        Name: String,
        Email: String,
        MobileNumber: String,
        DeliveryAddress: String
    ) {
        sharedPreferences.edit().putString("Name", Name).apply()
        sharedPreferences.edit().putString("Email", Email).apply()
        sharedPreferences.edit().putString("MobileNumber", MobileNumber).apply()
        sharedPreferences.edit().putString("DeliveryAddress", DeliveryAddress).apply()
    }

    override fun onPause() {
        super.onPause()
    }
}