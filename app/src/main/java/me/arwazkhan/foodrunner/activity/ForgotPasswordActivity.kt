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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var etMobileNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {

            if (etMobileNumber.text.isBlank() || etEmail.text.isBlank()) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please fill above details...",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (ConnectionManager().checkConnectivity(applicationContext)) {
                    try {
                        val loginUser = JSONObject()
                        loginUser.put("mobile_number", etMobileNumber.text)
                        loginUser.put("email", etEmail.text)

                        val queue = Volley.newRequestQueue(applicationContext)
                        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                        val jsonObjectRequest = object : JsonObjectRequest(
                            Method.POST,
                            url,
                            loginUser,
                            Response.Listener {
                                val responseJsonObjectData = it.getJSONObject("data")
                                val success = responseJsonObjectData.getBoolean("success")

                                if (success) {
                                    val firstTry = responseJsonObjectData.getBoolean("first_try")
                                    if (firstTry) {
                                        Toast.makeText(
                                            applicationContext,
                                            "OTP Sent !!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        openResetPassword()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "OTP Sent already !!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        openResetPassword()
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Some Error occurred !!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Some Error occurred !!!",
                                    Toast.LENGTH_SHORT
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

                        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(15000, 1, 1f)
                        queue.add(jsonObjectRequest)

                    } catch (e: JSONException) {
                        Toast.makeText(
                            applicationContext,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
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
        }
    }

    fun openResetPassword() {
        val intent =
            Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
        intent.putExtra("MobileNumber", etMobileNumber.text.toString())
        startActivity(intent)
        finish()
    }

}