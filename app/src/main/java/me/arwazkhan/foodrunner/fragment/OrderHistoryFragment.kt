package me.arwazkhan.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.adapter.OrderHistoryAdapter
import me.arwazkhan.foodrunner.model.OrderHistory

class OrderHistoryFragment : Fragment() {
    private lateinit var progressLayoutOrderHistory: RelativeLayout
    private lateinit var recyclerViewOrderHistory: RecyclerView
    private lateinit var imgNoOrderPlaced: ImageView
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var llHasOrders: LinearLayout
    private var orderHistoryList = ArrayList<OrderHistory>()
    private lateinit var sharedPreferences: SharedPreferences
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        llHasOrders = view.findViewById(R.id.llHasOrders)
        recyclerViewOrderHistory = view.findViewById(R.id.recyclerViewOrderHistory)
        progressLayoutOrderHistory = view.findViewById(R.id.progressLayoutOrderHistory)
        progressLayoutOrderHistory.visibility = View.VISIBLE

        imgNoOrderPlaced = view.findViewById(R.id.imgNoOrderPlaced)
        sharedPreferences = (activity as Context).getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        userId = sharedPreferences.getString("user_id", null).toString()
        setItems(userId)
        return view
    }

    private fun setItems(userId: String) {

        val queue = Volley.newRequestQueue(context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        val jsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener {
                try {
                    progressLayoutOrderHistory.visibility = View.GONE
                    val responseJsonObjectData = it.getJSONObject("data")
                    val success = responseJsonObjectData.getBoolean("success")

                    if (success) {
                        val data = responseJsonObjectData.getJSONArray("data")
                        if (data.length() == 0) {
                            Toast.makeText(
                                context,
                                "No Orders Placed yet!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            llHasOrders.visibility = View.GONE
                            imgNoOrderPlaced.visibility = View.VISIBLE
                        } else {
                            for (i in 0 until data.length()) {
                                val itemJsonObject = data.getJSONObject(i)
                                val foodItems = itemJsonObject.getJSONArray("food_items")
                                val eachObject = OrderHistory(
                                    itemJsonObject.getString("order_id"),
                                    itemJsonObject.getString("restaurant_name"),
                                    itemJsonObject.getString("total_cost"),
                                    itemJsonObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistoryList.add(eachObject)

                                if (orderHistoryList.isEmpty()) {
                                    llHasOrders.visibility = View.GONE
                                    imgNoOrderPlaced.visibility = View.VISIBLE
                                } else {
                                    llHasOrders.visibility = View.VISIBLE
                                    imgNoOrderPlaced.visibility = View.INVISIBLE
                                    if (activity != null) {
                                        orderHistoryAdapter = OrderHistoryAdapter(
                                            activity as Context,
                                            orderHistoryList
                                        )
                                        recyclerViewOrderHistory.layoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerViewOrderHistory.itemAnimator =
                                            DefaultItemAnimator()
                                        recyclerViewOrderHistory.adapter = orderHistoryAdapter
                                    } else {
                                        queue.cancelAll(this::class.java.simpleName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                Toast.makeText(
                    context,
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
    }

}