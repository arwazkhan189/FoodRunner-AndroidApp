package me.arwazkhan.foodrunner

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class RegisterDetailsActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtMobileNumber: TextView
    lateinit var txtDeliveryAddress: TextView
    var Name: String? = ""
    var Email: String? = ""
    var MobileNumber: String? = ""
    var DeliveryAddress: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_register_details)

        title = "Registration Details"

        txtName = findViewById(R.id.txtName)
        Name = sharedPreferences.getString("Name", "")
        txtName.text = Name

        txtEmail = findViewById(R.id.txtEmail)
        Email = sharedPreferences.getString("Email", "")
        txtEmail.text = Email

        txtMobileNumber = findViewById(R.id.txtMobileNumber)
        MobileNumber = sharedPreferences.getString("MobileNumber", "")
        txtMobileNumber.text = MobileNumber

        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress)
        DeliveryAddress = sharedPreferences.getString("DeliveryAddress", "")
        txtDeliveryAddress.text = DeliveryAddress
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}