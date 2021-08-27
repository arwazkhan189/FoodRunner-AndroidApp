package me.arwazkhan.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.model.OrderHistory
import me.arwazkhan.foodrunner.model.RestaurantMenu
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(
    val context: Context,
    private val orderedRestaurantList: ArrayList<OrderHistory>
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistory>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderOrderHistory {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)
        return ViewHolderOrderHistory(view)
    }

    override fun getItemCount(): Int = orderedRestaurantList.size

    class ViewHolderOrderHistory(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderHistoryRestaurantName: TextView =
            view.findViewById(R.id.txtOrderHistoryRestaurantName)
        val txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        val recyclerViewOrderHistoryItems: RecyclerView =
            view.findViewById(R.id.recyclerViewOrderHistoryItems)
        val txtTotalCost: TextView = view.findViewById(R.id.txtTotalCost)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: ViewHolderOrderHistory,
        position: Int
    ) {
        val restaurantObject = orderedRestaurantList[position]
        holder.txtOrderHistoryRestaurantName.text = restaurantObject.restaurantName
        holder.txtOrderDate.text = formatDate(restaurantObject.orderPlacedAt)
        holder.txtTotalCost.text = "Rs. ${restaurantObject.totalCost}"
        setUpRecycler(holder.recyclerViewOrderHistoryItems, restaurantObject)
    }

    private fun formatDate(orderDate: String): CharSequence? {
        val inputFormat = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormat.parse(orderDate) as Date
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormat.format(date)
    }

    private fun setUpRecycler(
        recyclerViewOrderHistoryItems: RecyclerView,
        orderedRestaurantList: OrderHistory
    ) {
        val foodItemsList = ArrayList<RestaurantMenu>()
        for (i in 0 until orderedRestaurantList.foodItems.length()) {
            val foodJson = orderedRestaurantList.foodItems.getJSONObject(i)
            foodItemsList.add(
                RestaurantMenu(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val cartItemAdapter = CartAdapter(context, foodItemsList)
        recyclerViewOrderHistoryItems.layoutManager = LinearLayoutManager(context)
        recyclerViewOrderHistoryItems.itemAnimator = DefaultItemAnimator()
        recyclerViewOrderHistoryItems.adapter = cartItemAdapter
    }
}
