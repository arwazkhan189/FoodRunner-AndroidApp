package me.arwazkhan.foodrunner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        title = "Forgot Password"

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val MobileNumber = etMobileNumber.text.toString()
            val Email = etEmail.text.toString()

            if (MobileNumber.isNotEmpty() && Email.isNotEmpty()) {
                val intent =
                    Intent(this@ForgotPasswordActivity, ForgotPasswordDetailsActivity::class.java)
                intent.putExtra("MobileNumber", MobileNumber)
                intent.putExtra("Email", Email)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please fill above details...",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }
}