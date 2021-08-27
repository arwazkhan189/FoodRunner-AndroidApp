package me.arwazkhan.foodrunner.activity

import com.google.gson.Gson
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.adapter.CartAdapter
import me.arwazkhan.foodrunner.adapter.RestaurantMenuAdapter
import me.arwazkhan.foodrunner.database.FoodDatabase
import me.arwazkhan.foodrunner.database.OrderEntity
import me.arwazkhan.foodrunner.model.RestaurantMenu
import me.arwazkhan.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MyCartActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var progressLayoutCart: RelativeLayout
    private lateinit var txtOrderingFrom: TextView
    private lateinit var recyclerViewCart: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var btnPlaceOrder: Button
    private lateinit var restaurantId: String
    private lateinit var restaurantName: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var rlCart: RelativeLayout
    private var cartListItems = arrayListOf<RestaurantMenu>()
    private lateinit var menuAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        progressLayoutCart = findViewById(R.id.progressLayoutCart)
        txtOrderingFrom = findViewById(R.id.txtOrderingFrom)
        recyclerViewCart = findViewById(R.id.recyclerViewCart)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        rlCart = findViewById(R.id.rlCart)
        progressLayoutCart.visibility = View.GONE
        btnPlaceOrder.visibility = View.VISIBLE
        mLayoutManager = LinearLayoutManager(this@MyCartActivity)

        setToolBar()

        if (intent != null) {
            restaurantId = intent.getStringExtra("id").toString()
            restaurantName = intent.getStringExtra("res_name").toString()
            txtOrderingFrom.text = restaurantName
        } else {
            finish()
            Toast.makeText(
                this,
                "Some unexpected error occurred!!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (restaurantId == "0") {
            finish()
            Toast.makeText(
                this,
                "Some unexpected error occurred!!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        fetchData()
        placeOrder()
    }

    private fun setToolBar() {
        toolbar = findViewById(R.id.toolbarCart)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchData() {
        val itemList = GetItemsDBAsync(applicationContext).execute().get()
        for (i in itemList)
            cartListItems.addAll(
                Gson().fromJson(i.foodItems, Array<RestaurantMenu>::class.java).asList()
            )

        if (cartListItems.isEmpty()) {
            rlCart.visibility = View.GONE
            progressLayoutCart.visibility = View.VISIBLE
        } else {
            rlCart.visibility = View.VISIBLE
            progressLayoutCart.visibility = View.GONE
        }
        menuAdapter = CartAdapter(this@MyCartActivity, cartListItems)
        mLayoutManager = LinearLayoutManager(this@MyCartActivity)
        recyclerViewCart.layoutManager = mLayoutManager
        recyclerViewCart.itemAnimator = DefaultItemAnimator()
        recyclerViewCart.adapter = menuAdapter
    }

    class GetItemsDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }
    }

    class ClearDBAsync(context: Context, private val resId: String) :
        AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }
    }


    private fun placeOrder() {

        var sum = 0
        for (i in 0 until cartListItems.size)
            sum += cartListItems[i].cost_for_one.toInt()

        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            progressLayoutCart.visibility = View.VISIBLE
            sendRequest()
        }
    }

    private fun sendRequest() {
        val sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            MODE_PRIVATE
        )
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        if (ConnectionManager().checkConnectivity(this)) {
            try {
                progressLayoutCart.visibility = View.GONE
                val sendOrder = JSONObject()
                sendOrder.put("user_id", sharedPreferences.getString("user_id", "0"))
                sendOrder.put("restaurant_id", restaurantId)

                var total = 0
                for (i in 0 until cartListItems.size)
                    total += cartListItems[i].cost_for_one.toInt()
                sendOrder.put("total_cost", total.toString())

                val foodJsonArray = JSONArray()
                for (i in 0 until cartListItems.size) {
                    val singleItemObject = JSONObject()
                    singleItemObject.put("food_item_id", cartListItems[i].id)
                    foodJsonArray.put(i, singleItemObject)
                }
                sendOrder.put("food", foodJsonArray)

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    sendOrder,
                    Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            ClearDBAsync(applicationContext, restaurantId).execute().get()
                            RestaurantMenuAdapter.isCartEmpty = true
                            Toast.makeText(
                                this,
                                "Order Placed",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, OrderPlacedActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            rlCart.visibility = View.VISIBLE
                            Toast.makeText(
                                this,
                                "Order Failed due to server Error!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        rlCart.visibility = View.VISIBLE
                        Toast.makeText(
                            this,
                            "Some Error Occurred!!!",
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
                rlCart.visibility = View.VISIBLE
                Toast.makeText(
                    this,
                    "Some unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            checkInternet()
        }
    }

    override fun onBackPressed() {
        val alterDialog = AlertDialog.Builder(this)
        alterDialog.setTitle("Alert!")
        alterDialog.setMessage("Going back will remove everything from cart")
        alterDialog.setPositiveButton("Okay") { _, _ ->
            ClearDBAsync(applicationContext, restaurantId).execute().get()
            RestaurantMenuAdapter.isCartEmpty = true
            super.onBackPressed()
        }
        alterDialog.setNegativeButton("No") { _, _ ->

        }
        alterDialog.show()
    }

    private fun checkInternet() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("No Internet")
        dialog.setMessage("Internet Connection is not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(applicationContext as Activity)
        }
        dialog.create()
        dialog.show()
    }

}



