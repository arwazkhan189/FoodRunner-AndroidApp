package me.arwazkhan.foodrunner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ForgotPasswordDetailsActivity : AppCompatActivity() {
    lateinit var txtMobileNumber: TextView
    lateinit var txtEmail: TextView
    var MobileNumber = ""
    var Email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_details)

        title = "Forgot Password Details"

        txtMobileNumber = findViewById(R.id.txtMobileNumber)
        MobileNumber = intent.getStringExtra("MobileNumber").toString()
        txtMobileNumber.text = MobileNumber

        txtEmail = findViewById(R.id.txtEmail)
        Email = intent.getStringExtra("Email").toString()
        txtEmail.text = Email

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}