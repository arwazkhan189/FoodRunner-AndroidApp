package me.arwazkhan.foodrunner.fragment


import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import me.arwazkhan.foodrunner.R
import me.arwazkhan.foodrunner.adapter.FavouriteRestaurantsRecyclerAdapter
import me.arwazkhan.foodrunner.database.FoodDatabase
import me.arwazkhan.foodrunner.database.FoodEntity

class FavouriteRestaurantsFragment : Fragment() {
    private lateinit var recyclerFavourite: RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FavouriteRestaurantsRecyclerAdapter
    private lateinit var imgFavBg: ImageView
    private lateinit var txtFavBg: TextView
    private var foodList = listOf<FoodEntity>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = LinearLayoutManager(activity as Context)

        foodList = RetrieveFavourites(activity as Context).execute().get()

        imgFavBg = view.findViewById(R.id.imgFavBg)
        txtFavBg = view.findViewById(R.id.txtFavBg)
        if (foodList.isEmpty()) {
            imgFavBg.setImageResource(R.drawable.heart)
            txtFavBg.text = "You do not have any favourite restaurants"
        }

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRestaurantsRecyclerAdapter(activity as Context, foodList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        return view
    }


    class RetrieveFavourites(@SuppressLint("StaticFieldLeak") val context: Context) :
        AsyncTask<Void, Void, List<FoodEntity>>() {

        override fun doInBackground(vararg p0: Void?): List<FoodEntity> {
            val db = Room.databaseBuilder(context, FoodDatabase::class.java, "foods").build()
            return db.foodDao().getAllFoods()
        }

    }
}