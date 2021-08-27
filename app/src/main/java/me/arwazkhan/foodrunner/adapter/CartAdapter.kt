package me.arwazkhan.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.model.RestaurantMenu

class CartAdapter(val context: Context, private val cartItems: ArrayList<RestaurantMenu>) :
    RecyclerView.Adapter<CartAdapter.ViewHolderCart>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun getItemViewType(position: Int): Int = position

    class ViewHolderCart(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrderItemName: TextView = view.findViewById(R.id.txtOrderItemName)
        val txtOrderItemPrice: TextView = view.findViewById(R.id.txtOrderItemPrice)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject = cartItems[position]
        holder.txtOrderItemName.text = cartItemObject.name
        holder.txtOrderItemPrice.text = "Rs. ${cartItemObject.cost_for_one}"
    }

}