package me.arwazkhan.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.model.RestaurantMenu

class RestaurantMenuAdapter(
    val context: Context,
    private val restaurantMenuList: ArrayList<RestaurantMenu>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderMenu>() {

    companion object {
        var isCartEmpty = true
    }

    class ViewHolderMenu(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNumber: TextView = view.findViewById(R.id.txtSerialNumber)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove: Button = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMenu {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)

        return ViewHolderMenu(view)
    }

    override fun getItemCount(): Int = restaurantMenuList.size

    override fun getItemViewType(position: Int): Int = position

    interface OnItemClickListener {
        fun onAddItemClick(menuObject: RestaurantMenu)
        fun onRemoveItemClick(menuObject: RestaurantMenu)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderMenu, position: Int) {
        val restaurantMenuItem = restaurantMenuList[position]

        holder.txtSerialNumber.text = (position + 1).toString()
        holder.txtItemName.text = restaurantMenuItem.name
        holder.txtItemPrice.text = "Rs. ${restaurantMenuItem.cost_for_one}"

        holder.btnAdd.setOnClickListener {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnAdd.visibility = View.GONE
            listener.onAddItemClick(restaurantMenuItem)
        }

        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            listener.onRemoveItemClick(restaurantMenuItem)
        }
    }

}