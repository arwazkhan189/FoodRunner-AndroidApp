package me.arwazkhan.foodrunner


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class HomeActivity : AppCompatActivity() {
    lateinit var txtWelcomeUser: TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var btnLogout: Button
    var mobileno: String? = "1234567890"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_home)

        title = "Home"

        mobileno = sharedPreferences.getString("mobileno", "1234567890")
        txtWelcomeUser = findViewById(R.id.txtWelcomeUser)
        txtWelcomeUser.text = mobileno

        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
            sharedPreferences.edit().clear().apply()
            finish()
        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}