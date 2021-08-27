package me.arwazkhan.foodrunner.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var etOTP: EditText
    private lateinit var etOTPNewPassword: EditText
    private lateinit var etOTPConfirmPassword: EditText
    private lateinit var btnOTPSubmit: Button
    private var mobileNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etOTP = findViewById(R.id.etOTP)
        etOTPNewPassword = findViewById(R.id.etOTPNewPassword)
        etOTPConfirmPassword = findViewById(R.id.etOTPConfirmPassword)
        btnOTPSubmit = findViewById(R.id.btnOTPSubmit)

        btnOTPSubmit.setOnClickListener {
            if (checkError()) {
                Toast.makeText(
                    applicationContext,
                    "Password didn't matched or Above details are not filled !!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                resetPassword()
            }
        }
    }

    private fun resetPassword() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            try {
                val loginUser = JSONObject()
                mobileNumber = intent.getStringExtra("MobileNumber").toString()

                loginUser.put("mobile_number", mobileNumber)
                loginUser.put("password", etOTPNewPassword.text.toString())
                loginUser.put("otp", etOTP.text.toString())

                val queue = Volley.newRequestQueue(applicationContext)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    loginUser,
                    Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            val serverMessage = responseJsonObjectData.getString("successMessage")
                            Toast.makeText(
                                applicationContext,
                                serverMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            gotoLogin()
                        } else {
                            val errorMessage = responseJsonObjectData.getString("errorMessage")
                            Toast.makeText(
                                applicationContext,
                                errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred !!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = getString(R.string.token)
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            } catch (e: JSONException) {
                Toast.makeText(
                    applicationContext,
                    "Some unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
            dialog.setTitle("No Internet")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(applicationContext as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun gotoLogin() {
        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkError(): Boolean {
        return etOTP.text.isBlank() ||
                etOTPNewPassword.text.isBlank() ||
                etOTPConfirmPassword.text.isBlank() ||
                (etOTPNewPassword.text.toString() != etOTPConfirmPassword.text.toString())
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}