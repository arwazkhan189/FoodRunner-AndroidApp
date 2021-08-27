package me.arwazkhan.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


class RegistrationActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobileNumber: EditText
    private lateinit var etDeliveryAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnBackReg: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.et_delivery_address)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBackReg = findViewById(R.id.btnBackReg)

        btnBackReg.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {
            if (checkError()) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Passwords are not matched or Details are not filled.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                registerUser()
            }
        }

    }

    private fun registerUser() {
        val sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

        if (ConnectionManager().checkConnectivity(applicationContext)) {
            try {
                val registerUser = JSONObject()
                registerUser.put("name", etName.text)
                registerUser.put("mobile_number", etMobileNumber.text)
                registerUser.put("password", etPassword.text)
                registerUser.put("address", etDeliveryAddress.text)
                registerUser.put("email", etEmail.text)

                val queue = Volley.newRequestQueue(applicationContext)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    registerUser,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            val data = responseJsonObjectData.getJSONObject("data")

                            savePreferences(data)

                            Toast.makeText(
                                applicationContext,
                                "Registered successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Please enter unique Email and Mobile Number !!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            applicationContext,
                            "Some Error occurred!!!",
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
                Toast.makeText(
                    applicationContext,
                    "Some unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val dialog = AlertDialog.Builder(this@RegistrationActivity)
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

    private fun checkError(): Boolean {
        return etName.text.isBlank() ||
                etEmail.text.isBlank() ||
                etMobileNumber.text.isBlank() ||
                etDeliveryAddress.text.isBlank() ||
                etPassword.text.isBlank() ||
                etConfirmPassword.text.isBlank() ||
                (etPassword.text.toString() !=
                        etConfirmPassword.text.toString())
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
}