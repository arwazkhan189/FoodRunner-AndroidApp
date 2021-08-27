package me.arwazkhan.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtforgotPassword: TextView
    private lateinit var txtregister: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //initialization
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPasswordLogin)
        btnLogin = findViewById(R.id.btnLogin)
        txtforgotPassword = findViewById(R.id.txtforgot_password)
        txtregister = findViewById(R.id.txtregister)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Click listeners
        btnLogin.setOnClickListener {
            when {
                etMobileNumber.text.isBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please Enter your Mobile Number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                etPassword.text.isBlank() -> {
                    Toast.makeText(
                        applicationContext,
                        "Please Enter your Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    validateUser()
                }
            }
        }

        txtforgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtregister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUser() {
        if (ConnectionManager().checkConnectivity(applicationContext)) {
            try {
                val loginUser = JSONObject()

                loginUser.put("mobile_number", etMobileNumber.text)
                loginUser.put("password", etPassword.text)

                val queue = Volley.newRequestQueue(applicationContext)
                val url = "http://13.235.250.119/v2/login/fetch_result/"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    loginUser,
                    Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            val data = responseJsonObjectData.getJSONObject("data")

                            savePreferences(data)

                            Toast.makeText(
                                applicationContext,
                                "Welcome ${data.getString("name")}",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Invalid Credentials!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            applicationContext,
                            "Some error occurred!!!",
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
                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                btnLogin.visibility = View.VISIBLE
                Toast.makeText(
                    applicationContext,
                    "Some unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            checkInternet()
        }
    }

    override fun onResume() {
        if (!ConnectionManager().checkConnectivity(applicationContext)) {
            checkInternet()
        }
        super.onResume()
    }

    private fun savePreferences(
        data: JSONObject
    ) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit()
            .putString("user_id", data.getString("user_id")).apply()

        sharedPreferences.edit().putString("name", data.getString("name"))
            .apply()
        sharedPreferences.edit().putString("email", data.getString("email"))
            .apply()
        sharedPreferences.edit()
            .putString("mobile_number", data.getString("mobile_number")).apply()
        sharedPreferences.edit()
            .putString("address", data.getString("address")).apply()
    }

    private fun checkInternet() {
        val dialog = AlertDialog.Builder(this@LoginActivity)
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
