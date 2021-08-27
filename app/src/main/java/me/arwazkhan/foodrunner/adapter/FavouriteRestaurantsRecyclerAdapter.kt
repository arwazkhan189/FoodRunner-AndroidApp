package me.arwazkhan.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.activity.MenuActivity
import me.arwazkhan.foodrunner.database.FoodEntity

class FavouriteRestaurantsRecyclerAdapter(
    val context: Context,
    private val foodList: List<FoodEntity>
) : RecyclerView.Adapter<FavouriteRestaurantsRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_fav_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val food = foodList[position]

        val foodEntity = FoodEntity(
            food.id,
            food.foodName,
            food.foodRating,
            food.foodPrice,
            food.foodImage
        )

        holder.imgFavourite.setOnClickListener {
            if (!HomeRecyclerAdapter.DBASyncTask(context, foodEntity, 1).execute().get()) {
                val async = HomeRecyclerAdapter.DBASyncTask(context, foodEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_fav_fill)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = HomeRecyclerAdapter.DBASyncTask(context, foodEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Removed favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_fav_outline)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.llContent.setOnClickListener {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("id", food.id)
            intent.putExtra("res_name", food.foodName)
            context.startActivity(intent)
        }


        holder.txtRestaurantName.text = food.foodName
        holder.txtRestaurantPrice.text = "${food.foodPrice}/person"
        holder.textViewRating.text = food.foodRating
        holder.imgFavourite.setImageResource(R.drawable.ic_fav_fill)
        Picasso.get().load(food.foodImage).error(R.drawable.foodrunner)
            .into(holder.imgFoodImage)

        val checkFav = HomeRecyclerAdapter.DBASyncTask(context, foodEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgFavourite.setImageResource(R.drawable.ic_fav_fill)

        } else {
            holder.imgFavourite.setImageResource(R.drawable.ic_fav_outline)
        }
    }

}
