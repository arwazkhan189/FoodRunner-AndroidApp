package me.arwazkhan.foodrunner.activity

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
import android.widget.ImageView
import android.widget.RelativeLayout
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
import com.google.gson.Gson
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.adapter.HomeRecyclerAdapter
import me.arwazkhan.foodrunner.adapter.RestaurantMenuAdapter
import me.arwazkhan.foodrunner.database.FoodDatabase
import me.arwazkhan.foodrunner.database.OrderEntity
import me.arwazkhan.foodrunner.model.RestaurantMenu
import me.arwazkhan.foodrunner.util.ConnectionManager
import org.json.JSONException

class MenuActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var rlMenuProgress: RelativeLayout
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var rlProceedToCart: RelativeLayout
    private lateinit var btnProceedToCart: Button
    private lateinit var menuAdapter: RestaurantMenuAdapter
    private lateinit var restaurantId: String
    private lateinit var restaurantName: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imgFavIcon: ImageView
    var restaurantMenuList = arrayListOf<RestaurantMenu>()
    var orderList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        toolbar = findViewById(R.id.toolbarMenu)

        rlMenuProgress = findViewById(R.id.rlMenuProgress)
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu)

        rlProceedToCart = findViewById(R.id.rlProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        imgFavIcon = findViewById(R.id.imgFavIcon)

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            MODE_PRIVATE
        )

        if (intent != null) {
            restaurantId = intent.getStringExtra("id").toString()
            restaurantName = intent.getStringExtra("res_name").toString()

            val fav = HomeRecyclerAdapter.GetFavAsyncTask(this).execute().get()
            if (fav.isNotEmpty() && fav.contains(restaurantId)) {
                imgFavIcon.setBackgroundResource(R.drawable.ic_fav_fill)
            } else {
                imgFavIcon.setBackgroundResource(R.drawable.ic_fav_outline)
            }
        } else {
            finish()
            Toast.makeText(
                this,
                "Some unexpected Error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (restaurantId == "100") {
            finish()
            Toast.makeText(
                this,
                "Some unexpected Error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setToolBar()

        fetchData()
        btnProceedToCart.setOnClickListener {
            proceedToCart()
        }

    }

    private fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun fetchData() {
        if (ConnectionManager().checkConnectivity(this)) {
            rlMenuProgress.visibility = View.VISIBLE
            try {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {
                        val responseJsonData = it.getJSONObject("data")
                        val success = responseJsonData.getBoolean("success")

                        if (success) {
                            orderList.clear()

                            val data = responseJsonData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val foodJsonObject = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one")
                                )
                                restaurantMenuList.add(menuObject)

                                menuAdapter = RestaurantMenuAdapter(
                                    this,
                                    restaurantMenuList,
                                    object : RestaurantMenuAdapter.OnItemClickListener {
                                        override fun onAddItemClick(menuObject: RestaurantMenu) {
                                            orderList.add(menuObject)
                                            if (orderList.size > 0) {
                                                btnProceedToCart.visibility = View.VISIBLE
                                                RestaurantMenuAdapter.isCartEmpty = false
                                            }
                                        }

                                        override fun onRemoveItemClick(menuObject: RestaurantMenu) {
                                            orderList.remove(menuObject)
                                            if (orderList.isEmpty()) {
                                                btnProceedToCart.visibility = View.GONE
                                                RestaurantMenuAdapter.isCartEmpty = true
                                            }
                                        }
                                    }
                                )
                                layoutManager = LinearLayoutManager(this)
                                recyclerViewMenu.adapter = menuAdapter
                                recyclerViewMenu.itemAnimator = DefaultItemAnimator()
                                recyclerViewMenu.layoutManager = layoutManager
                            }
                        }
                        rlMenuProgress.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        rlMenuProgress.visibility = View.INVISIBLE
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
                    this,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            checkInternet()
        }
    }

    private fun proceedToCart() {
        val gson = Gson()
        val foodItems = gson.toJson(orderList)
        val async = CartItems(this@MenuActivity, restaurantId, foodItems, 1).execute()
        val result = async.get()
        if (result) {
            val intent = Intent(this, MyCartActivity::class.java)
            intent.putExtra("id", restaurantId)
            intent.putExtra("res_name", restaurantName)
            startActivity(intent)
        } else {
            Toast.makeText(
                this@MenuActivity,
                "Some error occurred!!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    class CartItems(
        context: Context,
        private val restaurantId: String,
        private val foodItems: String,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    override fun onBackPressed() {
        if (orderList.size > 0) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Alert!")
            dialog.setMessage("Going back will remove everything from the cart")
            dialog.setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }
            dialog.setNegativeButton("No") { _, _ ->
            }
            dialog.show()
        } else {
            super.onBackPressed()
        }
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

    private fun checkInternet() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("No Internet")
        dialog.setMessage("Internet Connection is not Found")
        dialog.setPositiveButton("Open Settings") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(settingsIntent)
        }
        dialog.setNegativeButton("Exit") { _, _ ->
            ActivityCompat.finishAffinity(this)
        }
        dialog.create()
        dialog.show()
    }

}