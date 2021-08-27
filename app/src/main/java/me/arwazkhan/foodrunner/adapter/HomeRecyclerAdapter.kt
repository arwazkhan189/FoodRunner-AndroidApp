package me.arwazkhan.foodrunner.adapter

import android.annotation.SuppressLint
import me.arwazkhan.foodrunner.activity.MenuActivity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.database.FoodDatabase
import me.arwazkhan.foodrunner.database.FoodEntity
import me.arwazkhan.foodrunner.model.Restaurant

class HomeRecyclerAdapter(val context: Context, private var itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val food = itemList[position]

        val foodEntity = FoodEntity(
            food.foodId,
            food.foodName,
            food.foodRating,
            food.foodPrice,
            food.foodImage
        )


        holder.imgFavourite.setOnClickListener {
            if (!DBASyncTask(context, foodEntity, 1).execute().get()) {
                val async = DBASyncTask(context, foodEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_fav_fill)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBASyncTask(context, foodEntity, 3).execute()
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
            intent.putExtra("id", food.foodId)
            intent.putExtra("res_name", food.foodName)
            context.startActivity(intent)
        }


        holder.txtRestaurantName.text = food.foodName
        holder.textViewRating.text = food.foodRating
        holder.txtRestaurantPrice.text = "${food.foodPrice}/person"
        Picasso.get().load(food.foodImage).error(R.drawable.foodrunner_logo)
            .into(holder.imgFoodImage)

        val checkFav = DBASyncTask(context, foodEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.imgFavourite.setImageResource(R.drawable.ic_fav_fill)

        } else {
            holder.imgFavourite.setImageResource(R.drawable.ic_fav_outline)
        }
    }

    class DBASyncTask(
        @SuppressLint("StaticFieldLeak") private val context: Context,
        private val foodEntity: FoodEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods").build()
        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    val food: FoodEntity? = db.foodDao().getFoodByID(foodEntity.id)
                    db.close()
                    return food != null
                }
                2 -> {
                    db.foodDao().insertFood(foodEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.foodDao().deleteFood(foodEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    class GetFavAsyncTask(context: Context) : AsyncTask<Void, Void, List<String>>() {
        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods")
            .build()

        override fun doInBackground(vararg params: Void?): List<String> {
            val list = db.foodDao().getAllFoods()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id)
            }
            return listOfIds
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: ArrayList<Restaurant>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

}