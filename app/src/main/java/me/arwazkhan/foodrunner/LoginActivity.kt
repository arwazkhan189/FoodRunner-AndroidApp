package me.arwazkhan.foodrunner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtforgot_password: TextView
    lateinit var txtregister: TextView
    lateinit var sharedPreferences: SharedPreferences
    val validMobileNumber = "7240974211"
    val validPassword = "arwaz"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = "Food Runner"

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtforgot_password = findViewById(R.id.txtforgot_password)
        txtregister = findViewById(R.id.txtregister)

        btnLogin.setOnClickListener {
            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            val intent = Intent(this@LoginActivity, HomeActivity::class.java)

            if (mobileNumber == validMobileNumber) {
                when (password) {
                    validPassword -> {
                        savePreferences(mobileNumber)
                        startActivity(intent)
                    }
                    else -> Toast.makeText(
                        this@LoginActivity,
                        "Incorrect Password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Incorrect Credential", Toast.LENGTH_LONG).show()
            }
        }

        txtforgot_password.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtregister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onPause() {
        super.onPause()
    }

    private fun savePreferences(mobileno: String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("mobileno", mobileno).apply()
    }
}